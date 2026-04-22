package com.fsb.Controller.user;

import com.fsb.Service.EmailService;
import com.fsb.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/sendCode")
    public Result<Void> sendCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        emailService.sendCode(email);
        return Result.success();
    }
}
