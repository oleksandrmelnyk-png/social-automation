package com.kayleighrichmond.social_automation.service.api.social;

import com.kayleighrichmond.social_automation.service.api.account.AccountCreator;
import com.kayleighrichmond.social_automation.domain.type.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SocialRegistry {

    private final Map<Platform, AccountCreator> creatorsByPlatform;

    public SocialRegistry(Set<AccountCreator> accountCreators) {
        this.creatorsByPlatform = accountCreators.stream()
                .collect(Collectors.toMap(AccountCreator::getPlatform, Function.identity()));
    }

    public AccountCreator getAccountCreator(Platform platform) {
        return Optional.ofNullable(creatorsByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No accounts creator exists by platform: " + platform));
    }
}
