package com.kayleighrichmond.social_automation.system.controller.dto;

import com.kayleighrichmond.social_automation.common.type.Platform;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
public class CreateAccountsRequest {

    @NotNull(message = "Platform is required")
    private Platform platform;

    @Min(value = 1, message = "Required minimum 1 account to create")
    private int amount;

    @NotNull(message = "Country code is required")
    private String countryCode;
}
