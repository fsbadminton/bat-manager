package com.fsb.Controller.ai;

import com.fsb.Service.PlatformIdentityService;
import com.fsb.Mapper.UserMapper;
import com.fsb.auth.AiContextSignature;
import com.fsb.auth.AiRequestReplayGuard;
import com.fsb.pojo.DTO.PlatformBindingConfirmRequest;
import com.fsb.pojo.VO.PlatformBindingVO;
import com.fsb.pojo.VO.AiResolvedIdentityVO;
import com.fsb.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal/ai/identity")
public class AiIdentityController {
    private final PlatformIdentityService identityService;
    private final AiContextSignature contextSignature;
    private final AiRequestReplayGuard replayGuard;
    private final UserMapper userMapper;

    public AiIdentityController(PlatformIdentityService identityService, AiContextSignature contextSignature,
                                AiRequestReplayGuard replayGuard, UserMapper userMapper) {
        this.identityService = identityService;
        this.contextSignature = contextSignature;
        this.replayGuard = replayGuard;
        this.userMapper = userMapper;
    }

    @PostMapping("/confirm")
    public Result<PlatformBindingVO> confirm(@Valid @RequestBody PlatformBindingConfirmRequest request,
                                             @RequestHeader("X-AI-Context-Timestamp") String timestamp,
                                             @RequestHeader("X-AI-Context-Nonce") String nonce,
                                             @RequestHeader("X-AI-Is-DM") boolean isDm,
                                             @RequestHeader("X-AI-Context-Signature") String signature) {
        contextSignature.verifyConfirmation(request.getPlatform(), request.getExternalUserId(), request.getToken(),
                request.getIdempotencyKey(), isDm, timestamp, nonce, signature);
        replayGuard.consume(nonce);
        if (!isDm) {
            throw new com.fsb.exception.AuthException(403, "FORBIDDEN: platform binding is private-chat only");
        }
        return Result.success(identityService.confirmBinding(request));
    }

    @GetMapping("/status")
    public Result<AiResolvedIdentityVO> status(@RequestHeader("X-AI-Platform") String platform,
                                            @RequestHeader("X-AI-External-User-Id") String externalUserId,
                                            @RequestHeader("X-AI-Is-DM") boolean isDm,
                                            @RequestHeader("X-AI-Context-Timestamp") String timestamp,
                                            @RequestHeader("X-AI-Context-Nonce") String nonce,
                                            @RequestHeader("X-AI-Context-Signature") String signature) {
        contextSignature.verifyIdentity(platform, externalUserId, isDm, timestamp, nonce, signature);
        replayGuard.consume(nonce);
        if (!isDm) {
            throw new com.fsb.exception.AuthException(403, "FORBIDDEN: identity lookup is private-chat only");
        }
        return Result.success(identityService.resolveAiIdentity(platform, externalUserId));
    }

    @GetMapping("/web/status")
    public Result<Map<String, Object>> webStatus(@RequestHeader("X-AI-Web-User-Id") String userId,
                                                 @RequestHeader("X-AI-Context-Timestamp") String timestamp,
                                                 @RequestHeader("X-AI-Context-Nonce") String nonce,
                                                 @RequestHeader("X-AI-Context-Signature") String signature) {
        contextSignature.verifyWebUser(userId, timestamp, nonce, signature);
        replayGuard.consume(nonce);
        Long parsedUserId;
        try {
            parsedUserId = Long.valueOf(userId);
        } catch (NumberFormatException ex) {
            throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: invalid web user id");
        }
        var user = userMapper.getById(parsedUserId);
        if (user == null || user.getStatus() != null && user.getStatus() == 0) {
            throw new com.fsb.exception.AuthException(401, "INVALID_CONTEXT: web user is unavailable");
        }
        return Result.success(Map.of("userId", user.getId(), "username", user.getUsername()));
    }
}
