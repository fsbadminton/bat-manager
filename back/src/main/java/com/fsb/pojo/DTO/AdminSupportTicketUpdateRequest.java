package com.fsb.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminSupportTicketUpdateRequest {
    @NotBlank
    @Pattern(regexp = "open|in_progress|pending_user|resolved|closed")
    private String status;

    @Size(max = 2000)
    private String adminNote;

    @Size(max = 2000)
    private String adminReply;
}
