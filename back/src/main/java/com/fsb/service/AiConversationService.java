package com.fsb.Service;

import com.fsb.pojo.DTO.AiMessagePersistRequest;
import com.fsb.pojo.VO.AiConversationVO;
import com.fsb.pojo.VO.AiDrawingVO;
import com.fsb.pojo.VO.AiMessageVO;

import java.util.List;

public interface AiConversationService {
    AiConversationVO getCurrent(Long userId, int limit);

    String resolveConversationId(Long userId);

    AiMessageVO append(Long userId, AiMessagePersistRequest request);

    void complete(Long userId, Long messageId, String content, String modelName, String toolsUsed);

    void fail(Long userId, Long messageId, String errorCode);

    List<AiDrawingVO> listDrawings(Long userId, int limit);
}
