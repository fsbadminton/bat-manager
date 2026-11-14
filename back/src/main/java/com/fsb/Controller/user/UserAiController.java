package com.fsb.Controller.user;

import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.AiChatRequest;
import com.fsb.result.Result;
import com.fsb.service.AiChatService;
import com.fsb.service.AiConversationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;
import java.util.List;
import com.fsb.pojo.VO.AiConversationVO;
import com.fsb.pojo.VO.AiDrawingVO;

@RestController
@RequestMapping("/user/ai")
public class UserAiController {

    private final AiChatService aiChatService;
    private final AiConversationService conversationService;

    public UserAiController(AiChatService aiChatService, AiConversationService conversationService) {
        this.aiChatService = aiChatService;
        this.conversationService = conversationService;
    }

    @GetMapping("/conversation")
    public Result<AiConversationVO> conversation(@RequestParam(defaultValue = "50") Integer limit) {
        AuthUser authUser = requireUser();
        return Result.success(conversationService.getCurrent(authUser.getUserId(), limit));
    }

    @GetMapping("/drawings")
    public Result<List<AiDrawingVO>> drawings(@RequestParam(defaultValue = "20") Integer limit) {
        AuthUser authUser = requireUser();
        return Result.success(conversationService.listDrawings(authUser.getUserId(), limit));
    }

    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@Valid @RequestBody AiChatRequest request) {
        AuthUser authUser = requireUser();
        return Result.success(aiChatService.chat(request, authUser.getUserId(), authUser.getUsername()));
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> chatStream(@Valid @RequestBody AiChatRequest request) {
        AuthUser authUser = requireUser();
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header("Cache-Control", "no-cache, no-transform")
                .header("X-Accel-Buffering", "no")
                .body(aiChatService.chatStream(request, authUser.getUserId(), authUser.getUsername()));
    }

    private AuthUser requireUser() {
        AuthUser authUser = AuthContext.getCurrentUser();
        if (authUser == null || authUser.getUserId() == null || authUser.getUsername() == null) {
            throw new AuthException(401, "Unauthorized");
        }
        return authUser;
    }
}
