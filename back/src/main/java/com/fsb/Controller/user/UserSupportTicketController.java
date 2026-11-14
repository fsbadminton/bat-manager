package com.fsb.Controller.user;

import com.fsb.Service.AiSupportTicketService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.exception.AuthException;
import com.fsb.pojo.DTO.AiTicketCreateRequest;
import com.fsb.pojo.VO.AiTicketVO;
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

@RestController
@RequestMapping("/user/support-tickets")
public class UserSupportTicketController {
    private final AiSupportTicketService tickets;

    public UserSupportTicketController(AiSupportTicketService tickets) {
        this.tickets = tickets;
    }

    @GetMapping
    public Result<List<AiTicketVO>> list(@RequestParam(defaultValue = "20") Integer limit) {
        if (limit == null || limit < 1 || limit > 20) {
            throw new IllegalArgumentException("limit must be between 1 and 20");
        }
        return Result.success(tickets.list(currentUserId(), limit));
    }

    @PostMapping
    public Result<AiTicketVO> create(@Valid @RequestBody AiTicketCreateRequest request) {
        return Result.success(tickets.create(currentUserId(), request));
    }

    @GetMapping("/{ticketNo}")
    public Result<AiTicketVO> detail(@PathVariable String ticketNo) {
        if (ticketNo == null || !ticketNo.matches("[A-Za-z0-9-]{3,24}")) {
            throw new IllegalArgumentException("ticket_no format is invalid");
        }
        return Result.success(tickets.get(currentUserId(), ticketNo));
    }

    private Long currentUserId() {
        AuthUser user = AuthContext.getCurrentUser();
        if (user == null || user.getUserId() == null) {
            throw new AuthException(401, "UNAUTHENTICATED: login required");
        }
        return user.getUserId();
    }
}
