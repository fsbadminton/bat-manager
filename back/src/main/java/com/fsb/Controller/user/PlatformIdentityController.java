package com.fsb.Controller.user;

import com.fsb.Service.PlatformIdentityService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.pojo.DTO.PlatformBindingUnbindRequest;
import com.fsb.pojo.DTO.PlatformBindingStartRequest;
import com.fsb.pojo.VO.PlatformBindingTokenVO;
import com.fsb.pojo.VO.PlatformBindingVO;
import com.fsb.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/identity-bindings")
public class PlatformIdentityController {
    private final PlatformIdentityService identityService;

    public PlatformIdentityController(PlatformIdentityService identityService) {
        this.identityService = identityService;
    }

    @PostMapping("/start")
    public Result<PlatformBindingTokenVO> start(@Valid @RequestBody PlatformBindingStartRequest request) {
        AuthUser user = currentUser();
        return Result.success(identityService.startBinding(user.getUserId(), request.getPlatform()));
    }

    @GetMapping
    public Result<List<PlatformBindingVO>> list() {
        return Result.success(identityService.listBindings(currentUser().getUserId()));
    }

    @DeleteMapping
    public Result<Void> unbind(@Valid @RequestBody PlatformBindingUnbindRequest request) {
        identityService.unbindByReference(currentUser().getUserId(), request.getPlatform(), request.getBindingReference());
        return Result.success();
    }

    private AuthUser currentUser() {
        AuthUser user = AuthContext.getCurrentUser();
        if (user == null || user.getUserId() == null) {
            throw new IllegalStateException("Unauthorized");
        }
        return user;
    }
}
