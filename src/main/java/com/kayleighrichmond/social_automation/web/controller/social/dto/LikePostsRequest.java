package com.kayleighrichmond.social_automation.web.controller.social.dto;

import com.kayleighrichmond.social_automation.domain.type.Platform;
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

    @Min(value = 1, message = "Minimum 1 action is required")
    @Max(value = 10, message = "Maximum 10 actions are allowed")
    private int likes;

}
