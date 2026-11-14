package com.fsb.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AiTicketCreateRequest {
    @NotBlank
    @Size(max = 120)
    private String title;

    @NotBlank
    @Size(max = 4000)
    private String description;

    @Pattern(regexp = "low|medium|high|urgent")
    private String priority = "medium";

    @NotBlank
    @Size(max = 128)
    private String idempotencyKey;
}
