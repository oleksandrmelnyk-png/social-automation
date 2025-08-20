package com.kayleighrichmond.social_automation.common.registry;

import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.common.LikeHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LikeHandlerRegistry {

    private final Map<Platform, LikeHandler> likeHandlersByPlatform;

    public LikeHandlerRegistry(Set<LikeHandler> likeHandlers) {
        this.likeHandlersByPlatform = likeHandlers.stream()
                .collect(Collectors.toMap(LikeHandler::getPlatform, Function.identity()));
    }

    public LikeHandler getLikeHandler(Platform platform) {
        return Optional.ofNullable(likeHandlersByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No like handlers exists by platform: " + platform));
    }
}
