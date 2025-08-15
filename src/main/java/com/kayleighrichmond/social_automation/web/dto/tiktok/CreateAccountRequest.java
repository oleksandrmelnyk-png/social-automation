package com.kayleighrichmond.social_automation.web.dto.tiktok;

import com.kayleighrichmond.social_automation.type.Platform;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotNull(message = "Platform is required")
    private Platform platform;

    @Min(value = 1, message = "Required minimum 1 account to create")
    @Max(value = 1, message = "Maximum 1 account to create")
    private int amount;

    private String countryCode;
}
