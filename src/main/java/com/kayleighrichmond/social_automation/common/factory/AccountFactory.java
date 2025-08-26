package com.kayleighrichmond.social_automation.common.factory;

import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.common.command.AccountCommand;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountFactory {

    private final Map<Platform, AccountCommand> accountCommandsByPlatform;

    public AccountFactory(Set<AccountCommand> accountCommands) {
        this.accountCommandsByPlatform = accountCommands.stream()
                .collect(Collectors.toMap(AccountCommand::getPlatform, Function.identity()));
    }

    public AccountCommand getAccountCommand(Platform platform) {
        return Optional.ofNullable(accountCommandsByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No account commands exists by platform: " + platform));
    }
}
