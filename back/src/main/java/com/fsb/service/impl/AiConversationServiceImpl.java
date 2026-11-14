package com.fsb.service.impl;

import com.fsb.Mapper.AiConversationMapper;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.AiMessagePersistRequest;
import com.fsb.pojo.VO.AiConversationVO;
import com.fsb.pojo.VO.AiDrawingVO;
import com.fsb.pojo.VO.AiMessageVO;
import com.fsb.pojo.entity.AiConversation;
import com.fsb.pojo.entity.AiMessage;
import com.fsb.service.AiConversationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AiConversationServiceImpl implements AiConversationService {
    private static final String SCENE = "customer_service";
    private final AiConversationMapper mapper;

    public AiConversationServiceImpl(AiConversationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public AiConversationVO getCurrent(Long userId, int limit) {
        validateLimit(limit);
        AiConversation conversation = getOrCreate(userId);
        return toView(conversation, limit);
    }

    @Override
    @Transactional
    public String resolveConversationId(Long userId) {
        return getOrCreate(userId).getConversationId();
    }

    @Override
    @Transactional
    public AiMessageVO append(Long userId, AiMessagePersistRequest request) {
        AiConversation conversation = mapper.findByConversationId(request.getConversationId());
        if (conversation == null || !userId.equals(conversation.getUserId())) {
            throw new AuthException(404, "NOT_FOUND: conversation not found");
        }
        AiMessage existing = mapper.findMessageByClientId(conversation.getId(), request.getClientMessageId());
        if (existing != null) {
            if (!sameMessage(existing, request)) {
                throw new AuthException(409, "IDEMPOTENCY_CONFLICT: message key reused with different content");
            }
            return AiMessageVO.from(existing);
        }
        mapper.advanceConversation(conversation.getId(), request.getChannel());
        Long sequence = mapper.currentVersion(conversation.getId());
        AiMessage message = new AiMessage();
        message.setConversationId(conversation.getId());
        message.setUserId(userId);
        message.setChannel(request.getChannel());
        message.setExternalChatId(blankToNull(request.getExternalChatId()));
        message.setExternalMessageId(blankToNull(request.getExternalMessageId()));
        message.setClientMessageId(request.getClientMessageId());
        message.setDirection(request.getDirection());
        message.setContentType(request.getContentType() == null ? "text" : request.getContentType());
        message.setContent(request.getContent().trim());
        message.setStatus(request.getStatus() == null ? "completed" : request.getStatus());
        message.setSequenceNo(sequence);
        message.setModelName(blankToNull(request.getModelName()));
        message.setToolsUsed(blankToNull(request.getToolsUsed()));
        mapper.insertMessage(message);
        AiMessage created = mapper.findMessageByClientId(conversation.getId(), request.getClientMessageId());
        if (created == null) throw new IllegalStateException("AI message could not be persisted");
        return AiMessageVO.from(created);
    }

    @Override
    public void complete(Long userId, Long messageId, String content, String modelName, String toolsUsed) {
        if (messageId == null || content == null) throw new IllegalArgumentException("message completion is invalid");
        if (mapper.completeMessage(messageId, userId, content, blankToNull(modelName), blankToNull(toolsUsed)) == 0) {
            throw new AuthException(404, "NOT_FOUND: message not found");
        }
    }

    @Override
    public void fail(Long userId, Long messageId, String errorCode) {
        if (mapper.failMessage(messageId, userId, blankToNull(errorCode)) == 0) {
            throw new AuthException(404, "NOT_FOUND: message not found");
        }
    }

    @Override
    public List<AiDrawingVO> listDrawings(Long userId, int limit) {
        validateLimit(limit);
        return mapper.listDrawings(userId, limit).stream().map(AiDrawingVO::from).toList();
    }

    private AiConversation getOrCreate(Long userId) {
        AiConversation conversation = mapper.findByUserAndScene(userId, SCENE);
        if (conversation != null) return conversation;
        conversation = new AiConversation();
        conversation.setConversationId("cs-" + UUID.randomUUID().toString().replace("-", ""));
        conversation.setUserId(userId);
        conversation.setScene(SCENE);
        mapper.insertConversation(conversation);
        AiConversation created = mapper.findByUserAndScene(userId, SCENE);
        if (created == null) throw new IllegalStateException("AI conversation could not be persisted");
        return created;
    }

    private AiConversationVO toView(AiConversation conversation, int limit) {
        return AiConversationVO.from(conversation,
                mapper.listMessages(conversation.getId(), limit).stream().map(AiMessageVO::from).toList());
    }

    private boolean sameMessage(AiMessage existing, AiMessagePersistRequest request) {
        return existing.getUserId() != null
                && existing.getChannel().equals(request.getChannel())
                && existing.getDirection().equals(request.getDirection())
                && existing.getContent().equals(request.getContent().trim());
    }

    private void validateLimit(int limit) {
        if (limit < 1 || limit > 100) throw new IllegalArgumentException("limit must be between 1 and 100");
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
