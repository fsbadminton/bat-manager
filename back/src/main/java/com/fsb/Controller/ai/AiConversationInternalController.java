package com.fsb.Controller.ai;

import com.fsb.Service.PlatformIdentityService;
import com.fsb.Mapper.UserMapper;
import com.fsb.auth.AiContextSignature;
import com.fsb.auth.AiRequestReplayGuard;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.AiMessageCompletionRequest;
import com.fsb.pojo.DTO.AiMessagePersistRequest;
import com.fsb.pojo.VO.AiMessageVO;
import com.fsb.pojo.VO.AiConversationVO;
import com.fsb.result.Result;
import com.fsb.Service.AiConversationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal/ai")
public class AiConversationInternalController {
    private final AiConversationService conversationService;
    private final PlatformIdentityService identityService;
    private final AiContextSignature contextSignature;
    private final AiRequestReplayGuard replayGuard;
    private final UserMapper userMapper;

    public AiConversationInternalController(AiConversationService conversationService,
                                            PlatformIdentityService identityService,
                                            AiContextSignature contextSignature,
                                            AiRequestReplayGuard replayGuard,
                                            UserMapper userMapper) {
        this.conversationService = conversationService;
        this.identityService = identityService;
        this.contextSignature = contextSignature;
        this.replayGuard = replayGuard;
        this.userMapper = userMapper;
    }

    @GetMapping("/conversations/current")
    public Result<Map<String, Object>> currentConversation(
            @RequestParam(value = "limit", defaultValue = "20") Integer limit,
            @RequestHeader(value = "X-AI-Platform", required = false) String platform,
            @RequestHeader(value = "X-AI-External-User-Id", required = false) String externalUserId,
            @RequestHeader(value = "X-AI-Is-DM", required = false) Boolean isDm,
            @RequestHeader(value = "X-AI-Context-Timestamp") String timestamp,
            @RequestHeader(value = "X-AI-Context-Nonce") String nonce,
            @RequestHeader(value = "X-AI-Context-Signature") String signature,
            @RequestHeader(value = "X-AI-Web-User-Id", required = false) String webUserId) {
        Long userId = resolveUserId(platform, externalUserId, isDm, timestamp, nonce, signature, webUserId);
        AiConversationVO conversation = conversationService.getCurrent(userId, limit == null ? 20 : limit);
        return Result.success(Map.of("userId", userId,
                "conversationId", conversation.getConversationId(),
                "messages", conversation.getMessages()));
    }

    @PostMapping("/messages")
    public Result<AiMessageVO> append(@Valid @RequestBody AiMessagePersistRequest request,
                                      @RequestHeader(value = "X-AI-Platform", required = false) String platform,
                                      @RequestHeader(value = "X-AI-External-User-Id", required = false) String externalUserId,
                                      @RequestHeader(value = "X-AI-Is-DM", required = false) Boolean isDm,
                                      @RequestHeader(value = "X-AI-Context-Timestamp") String timestamp,
                                      @RequestHeader(value = "X-AI-Context-Nonce") String nonce,
                                      @RequestHeader(value = "X-AI-Context-Signature") String signature,
                                      @RequestHeader(value = "X-AI-Web-User-Id", required = false) String webUserId) {
        Long userId = resolveUserId(platform, externalUserId, isDm, timestamp, nonce, signature, webUserId);
        return Result.success(conversationService.append(userId, request));
    }

    @PostMapping("/messages/{messageId}/complete")
    public Result<Void> complete(@PathVariable Long messageId,
                                 @Valid @RequestBody AiMessageCompletionRequest request,
                                 @RequestHeader(value = "X-AI-Platform", required = false) String platform,
                                 @RequestHeader(value = "X-AI-External-User-Id", required = false) String externalUserId,
                                 @RequestHeader(value = "X-AI-Is-DM", required = false) Boolean isDm,
                                 @RequestHeader(value = "X-AI-Context-Timestamp") String timestamp,
                                 @RequestHeader(value = "X-AI-Context-Nonce") String nonce,
                                 @RequestHeader(value = "X-AI-Context-Signature") String signature,
                                 @RequestHeader(value = "X-AI-Web-User-Id", required = false) String webUserId) {
        Long userId = resolveUserId(platform, externalUserId, isDm, timestamp, nonce, signature, webUserId);
        conversationService.complete(userId, messageId, request.getContent(), request.getModelName(), request.getToolsUsed());
        return Result.success();
    }

    @PostMapping("/messages/{messageId}/fail")
    public Result<Void> fail(@PathVariable Long messageId,
                             @RequestBody Map<String, String> request,
                             @RequestHeader(value = "X-AI-Platform", required = false) String platform,
                             @RequestHeader(value = "X-AI-External-User-Id", required = false) String externalUserId,
                             @RequestHeader(value = "X-AI-Is-DM", required = false) Boolean isDm,
                             @RequestHeader(value = "X-AI-Context-Timestamp") String timestamp,
                             @RequestHeader(value = "X-AI-Context-Nonce") String nonce,
                             @RequestHeader(value = "X-AI-Context-Signature") String signature,
                             @RequestHeader(value = "X-AI-Web-User-Id", required = false) String webUserId) {
        Long userId = resolveUserId(platform, externalUserId, isDm, timestamp, nonce, signature, webUserId);
        conversationService.fail(userId, messageId, request == null ? null : request.get("errorCode"));
        return Result.success();
    }

    private Long resolveUserId(String platform, String externalUserId, Boolean isDm,
                               String timestamp, String nonce, String signature, String webUserId) {
        boolean hasPlatform = platform != null && !platform.isBlank();
        boolean hasExternal = externalUserId != null && !externalUserId.isBlank();
        boolean hasWeb = webUserId != null && !webUserId.isBlank();
        if (hasWeb && (hasPlatform || hasExternal) || hasPlatform != hasExternal) {
            throw new AuthException(401, "INVALID_CONTEXT: ambiguous AI identity context");
        }
        if (hasPlatform) {
            if (!"telegram".equalsIgnoreCase(platform) && !"qq".equalsIgnoreCase(platform)) {
                throw new AuthException(401, "INVALID_CONTEXT: unsupported platform");
            }
            boolean privateChat = Boolean.TRUE.equals(isDm);
            contextSignature.verifyIdentity(platform, externalUserId, privateChat, timestamp, nonce, signature);
            replayGuard.consume(nonce);
            if (!privateChat) throw new AuthException(403, "FORBIDDEN: personal data is private-chat only");
            return identityService.resolveAiIdentity(platform, externalUserId).getCustomerId();
        }
        if (hasWeb) {
            contextSignature.verifyWebUser(webUserId, timestamp, nonce, signature);
            replayGuard.consume(nonce);
            try {
                Long userId = Long.valueOf(webUserId);
                var user = userMapper.getById(userId);
                if (user == null || (user.getStatus() != null && user.getStatus() == 0)) {
                    throw new AuthException(401, "INVALID_CONTEXT: web user is unavailable");
                }
                return userId;
            } catch (NumberFormatException ex) {
                throw new AuthException(401, "INVALID_CONTEXT: invalid web user id");
            }
        }
        throw new AuthException(401, "INVALID_CONTEXT: signed AI identity required");
    }
}
