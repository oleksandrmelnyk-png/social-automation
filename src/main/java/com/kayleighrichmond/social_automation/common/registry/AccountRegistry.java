package com.kayleighrichmond.social_automation.common.registry;

import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.common.AccountCreator;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountRegistry {

    private final Map<Platform, AccountCreator> creatorsByPlatform;

    public AccountRegistry(Set<AccountCreator> accountCreators) {
        this.creatorsByPlatform = accountCreators.stream()
                .collect(Collectors.toMap(AccountCreator::getPlatform, Function.identity()));
    }

    public AccountCreator getAccountCreator(Platform platform) {
        return Optional.ofNullable(creatorsByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No account creators exists by platform: " + platform));
    }
}
