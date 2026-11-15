package com.fsb.Service;

import com.fsb.pojo.DTO.AiChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;

public interface AiChatService {
    Map<String, Object> chat(AiChatRequest request, Long userId, String username);

    StreamingResponseBody chatStream(AiChatRequest request, Long userId, String username);
}
