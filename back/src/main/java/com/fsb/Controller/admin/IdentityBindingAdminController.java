package com.fsb.Controller.admin;

import com.fsb.Mapper.IdentityBindingAuditMapper;
import com.fsb.Service.PlatformIdentityService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.pojo.DTO.AdminIdentityUnbindRequest;
import com.fsb.pojo.DTO.AdminIdentityMoveRequest;
import com.fsb.pojo.VO.PlatformBindingVO;
import com.fsb.pojo.entity.IdentityBindingAudit;
import com.fsb.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/identity-bindings")
public class IdentityBindingAdminController {
    private final PlatformIdentityService identityService;
    private final IdentityBindingAuditMapper auditMapper;
    public IdentityBindingAdminController(PlatformIdentityService identityService,
                                          IdentityBindingAuditMapper auditMapper) {
        this.identityService = identityService;
        this.auditMapper = auditMapper;
    }

    @GetMapping("/{customerId}")
    public Result<List<PlatformBindingVO>> list(@PathVariable Long customerId) {
        return Result.success(identityService.listBindings(customerId));
    }

    @GetMapping("/audit")
    public Result<List<Map<String, Object>>> audit(@RequestParam(required = false) Long customerId) {
        return Result.success(auditMapper.list(customerId));
    }

    @PostMapping("/{customerId}/unbind")
    public Result<Void> unbind(@PathVariable Long customerId,
                               @Valid @RequestBody AdminIdentityUnbindRequest request) {
        String operator = currentOperator();
        IdentityBindingAudit audit = new IdentityBindingAudit();
        audit.setOperatorName(operator);
        audit.setAction("ADMIN_UNBIND");
        audit.setCustomerId(customerId);
        audit.setPlatform(request.getPlatform());
        audit.setExternalUserIdHash(request.getBindingReference());
        audit.setReason(request.getReason().trim());
        audit.setOutcome("PENDING");
        auditMapper.insert(audit);
        try {
            identityService.unbindByReference(customerId, request.getPlatform(), request.getBindingReference());
            auditMapper.updateOutcome(audit.getId(), "SUCCEEDED");
            return Result.success();
        } catch (RuntimeException ex) {
            auditMapper.updateOutcome(audit.getId(), "FAILED");
            throw ex;
        }
    }

    @PostMapping("/{customerId}/move")
    public Result<Void> move(@PathVariable Long customerId,
                             @Valid @RequestBody AdminIdentityMoveRequest request) {
        String operator = currentOperator();
        IdentityBindingAudit audit = new IdentityBindingAudit();
        audit.setOperatorName(operator);
        audit.setAction("ADMIN_MOVE_IDENTITY");
        audit.setCustomerId(customerId);
        audit.setPlatform(request.getPlatform());
        audit.setExternalUserIdHash(request.getBindingReference());
        audit.setReason("target=" + request.getTargetCustomerId() + "; " + request.getReason().trim());
        audit.setOutcome("PENDING");
        auditMapper.insert(audit);
        try {
            identityService.moveBindingByReference(customerId, request.getTargetCustomerId(), request.getPlatform(),
                    request.getBindingReference());
            auditMapper.updateOutcome(audit.getId(), "SUCCEEDED");
            return Result.success();
        } catch (RuntimeException ex) {
            auditMapper.updateOutcome(audit.getId(), "FAILED");
            throw ex;
        }
    }

    private String currentOperator() {
        AuthUser user = AuthContext.getCurrentUser();
        if (user == null || user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalStateException("Unauthorized");
        }
        return user.getUsername();
    }

}
