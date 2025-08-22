package com.kayleighrichmond.social_automation.common.registry;

import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.common.LikeActionHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LikeHandlerRegistry {

    private final Map<Platform, LikeActionHandler> likeHandlersByPlatform;

    public LikeHandlerRegistry(Set<LikeActionHandler> likeActionHandlers) {
        this.likeHandlersByPlatform = likeActionHandlers.stream()
                .collect(Collectors.toMap(LikeActionHandler::getPlatform, Function.identity()));
    }

    public LikeActionHandler getLikeHandler(Platform platform) {
        return Optional.ofNullable(likeHandlersByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No like handlers exists by platform: " + platform));
    }
}
