package com.fsb.Controller.user;

import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.AiChatRequest;
import com.fsb.result.Result;
import com.fsb.service.AiChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;

@RestController
@RequestMapping("/user/ai")
public class UserAiController {

    private final AiChatService aiChatService;

    public UserAiController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@Valid @RequestBody AiChatRequest request) {
        AuthUser authUser = AuthContext.getCurrentUser();
        if (authUser == null || authUser.getUserId() == null || authUser.getUsername() == null) {
            throw new AuthException(401, "Unauthorized");
        }
        return Result.success(aiChatService.chat(request, authUser.getUserId(), authUser.getUsername()));
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> chatStream(@Valid @RequestBody AiChatRequest request) {
        AuthUser authUser = AuthContext.getCurrentUser();
        if (authUser == null || authUser.getUserId() == null || authUser.getUsername() == null) {
            throw new AuthException(401, "Unauthorized");
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header("Cache-Control", "no-cache, no-transform")
                .header("X-Accel-Buffering", "no")
                .body(aiChatService.chatStream(request, authUser.getUserId(), authUser.getUsername()));
    }
}
