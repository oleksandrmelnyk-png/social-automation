package com.kayleighrichmond.social_automation.common.factory;

import com.kayleighrichmond.social_automation.common.command.AccountActionProxyCommand;
import com.kayleighrichmond.social_automation.common.type.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProxyFactory {

    private final Map<Platform, AccountActionProxyCommand> proxyCommandsByPlatform;

    public ProxyFactory(Set<AccountActionProxyCommand> accountActionProxyCommands) {
        this.proxyCommandsByPlatform = accountActionProxyCommands.stream()
                .collect(Collectors.toMap(AccountActionProxyCommand::getPlatform, Function.identity()));
    }

    public AccountActionProxyCommand getProxyCommand(Platform platform) {
        return Optional.ofNullable(proxyCommandsByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No proxy commands exists by platform: " + platform));
    }

}
