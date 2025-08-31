package com.kayleighrichmond.social_automation.common.factory;

import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.common.command.AccountsCreationCommand;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountsCreationFactory {

    private final Map<Platform, AccountsCreationCommand> accountCommandsByPlatform;

    public AccountsCreationFactory(Set<AccountsCreationCommand> accountsCreationCommands) {
        this.accountCommandsByPlatform = accountsCreationCommands.stream()
                .collect(Collectors.toMap(AccountsCreationCommand::getPlatform, Function.identity()));
    }

    public AccountsCreationCommand getAccountCommand(Platform platform) {
        return Optional.ofNullable(accountCommandsByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No account commands exists by platform: " + platform));
    }
}
