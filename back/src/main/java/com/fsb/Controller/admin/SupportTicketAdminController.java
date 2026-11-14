package com.fsb.Controller.admin;

import com.fsb.Mapper.SupportTicketMapper;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.AdminSupportTicketUpdateRequest;
import com.fsb.pojo.entity.SupportTicket;
import com.fsb.pojo.VO.AdminSupportTicketVO;
import com.fsb.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/support-tickets")
public class SupportTicketAdminController {
    private final SupportTicketMapper ticketMapper;

    public SupportTicketAdminController(SupportTicketMapper ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    @GetMapping
    public Result<List<AdminSupportTicketVO>> list(@RequestParam(defaultValue = "50") Integer limit) {
        requireAdmin();
        if (limit == null || limit < 1 || limit > 200) throw new IllegalArgumentException("limit must be between 1 and 200");
        return Result.success(ticketMapper.listAll(limit).stream().map(AdminSupportTicketVO::from).toList());
    }

    @GetMapping("/{ticketNo}")
    public Result<AdminSupportTicketVO> detail(@PathVariable String ticketNo) {
        requireAdmin();
        SupportTicket ticket = ticketMapper.findAnyByTicketNo(ticketNo);
        if (ticket == null) throw new AuthException(404, "NOT_FOUND: ticket not found");
        return Result.success(AdminSupportTicketVO.from(ticket));
    }

    @PutMapping("/{ticketNo}")
    public Result<Void> update(@PathVariable String ticketNo,
                               @Valid @RequestBody AdminSupportTicketUpdateRequest request) {
        AuthUser admin = requireAdmin();
        if (ticketMapper.findAnyByTicketNo(ticketNo) == null) {
            throw new AuthException(404, "NOT_FOUND: ticket not found");
        }
        int updated = ticketMapper.updateStatus(ticketNo, request.getStatus(), request.getAdminNote(), request.getAdminReply(),
                admin.getUsername());
        if (updated == 0) {
            throw new AuthException(404, "NOT_FOUND: ticket not found");
        }
        return Result.success();
    }

    private AuthUser requireAdmin() {
        AuthUser user = AuthContext.getCurrentUser();
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new AuthException(403, "FORBIDDEN: admin role required");
        }
        return user;
    }
}
