package com.kayleighrichmond.social_automation.service.api.social;

import com.kayleighrichmond.social_automation.service.api.account.AccountCreator;
import com.kayleighrichmond.social_automation.domain.type.Platform;
import com.kayleighrichmond.social_automation.service.api.account.LikeHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SocialRegistry {

    private final Map<Platform, AccountCreator> creatorsByPlatform;

    private final Map<Platform, LikeHandler> likeHandlersByPlatform;

    public SocialRegistry(Set<AccountCreator> accountCreators, Set<LikeHandler> likeHandlers) {
        this.creatorsByPlatform = accountCreators.stream()
                .collect(Collectors.toMap(AccountCreator::getPlatform, Function.identity()));

        this.likeHandlersByPlatform = likeHandlers.stream()
                .collect(Collectors.toMap(LikeHandler::getPlatform, Function.identity()));
    }

    public AccountCreator getAccountCreator(Platform platform) {
        return Optional.ofNullable(creatorsByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No accounts creator exists by platform: " + platform));
    }

    public LikeHandler getLikeHandler(Platform platform) {
        return Optional.ofNullable(likeHandlersByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No like handlers exists by platform: " + platform));
    }
}
