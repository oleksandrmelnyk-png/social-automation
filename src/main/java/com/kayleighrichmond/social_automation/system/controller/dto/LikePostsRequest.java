package com.kayleighrichmond.social_automation.system.controller.dto;

import com.kayleighrichmond.social_automation.common.type.Platform;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikePostsRequest {

    @NotNull(message = "Platform is required")
    private Platform platform;

    @Min(value = 1, message = "Minimum 1 like is required")
    @Max(value = 30, message = "Maximum 30 likes are allowed")
    private int likesCount;

}
