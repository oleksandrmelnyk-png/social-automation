package com.kayleighrichmond.social_automation.common.factory;

import com.kayleighrichmond.social_automation.common.command.AccountsCreationProxyCommand;
import com.kayleighrichmond.social_automation.common.type.Platform;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountsCreationProxyFactory {

    private final Map<Platform, AccountsCreationProxyCommand> accountCreationProxyCommandsByPlatform;

    public AccountsCreationProxyFactory(Set<AccountsCreationProxyCommand> accountCreationProxyCommands) {
        this.accountCreationProxyCommandsByPlatform = accountCreationProxyCommands.stream()
                .collect(Collectors.toMap(AccountsCreationProxyCommand::getPlatform, Function.identity()));
    }

    public AccountsCreationProxyCommand getAccountCreationProxyCommand(Platform platform) {
        return Optional.ofNullable(accountCreationProxyCommandsByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No account proxy commands exists by platform: " + platform));
    }
}
