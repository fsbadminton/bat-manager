package com.fsb.Controller.user;

import com.fsb.Service.ReturnApplyService;
import com.fsb.Service.ReturnReasonService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.auth.PermissionConstants;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.ReturnReasonDTO;
import com.fsb.pojo.VO.OmsReturnApplyVO;
import com.fsb.pojo.VO.ReturnReasonVO;
import com.fsb.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/returnApply")
public class UserReturnApplyController {
    @Autowired
    private ReturnApplyService returnApplyService;

    @Autowired
    private ReturnReasonService returnReasonService;

    @GetMapping("/list")
    public Result<List<ReturnReasonVO>> list() {
        AuthUser authUser = requireAuthUser();
        requirePermission(authUser, PermissionConstants.REFUND_READ_OWN, "Refund read permission required");
        List<ReturnReasonVO> listReason = returnReasonService.listReason();
        return Result.success(listReason);
    }

    @PostMapping("/create")
    public Result<String> create(@RequestBody ReturnReasonDTO dto) {
        AuthUser authUser = requireAuthUser();
        requirePermission(authUser, PermissionConstants.REFUND_APPLY_OWN, "Refund apply permission required");
        dto.setName(authUser.getUsername());
        returnApplyService.createReturnApply(dto);
        return Result.success("Submitted");
    }

    @GetMapping("/listAll")
    public Result<List<OmsReturnApplyVO>> listAll() {
        AuthUser authUser = requireAuthUser();
        requirePermission(authUser, PermissionConstants.REFUND_READ_OWN, "Refund read permission required");
        List<OmsReturnApplyVO> list = returnApplyService.getUserReturnApplyList(authUser.getUsername());
        return Result.success(list);
    }

    private AuthUser requireAuthUser() {
        AuthUser authUser = AuthContext.getCurrentUser();
        if (authUser == null) {
            throw new AuthException(401, "Unauthorized");
        }
        return authUser;
    }

    private void requirePermission(AuthUser authUser, String permission, String message) {
        if (!authUser.hasPermission(permission)) {
            throw new AuthException(403, message);
        }
    }
}
