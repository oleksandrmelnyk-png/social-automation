package com.kayleighrichmond.social_automation.system.controller.dto;

import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.type.Platform;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProcessActionRequest {

    @NotNull(message = "Platform is required")
    private Platform platform;

    @NotNull(message = "Action is required")
    private Action action;

    @Min(value = 1, message = "Minimum actions is 1")
    @Max(value = 1, message = "Maximum actions is 1")
    private Integer actionsCount;
}
