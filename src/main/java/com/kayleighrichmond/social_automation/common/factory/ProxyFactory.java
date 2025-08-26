package com.kayleighrichmond.social_automation.common.factory;

import com.kayleighrichmond.social_automation.common.command.ProxyCommand;
import com.kayleighrichmond.social_automation.common.type.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProxyFactory {

    private final Map<Platform, ProxyCommand> proxyCommandsByPlatform;

    public ProxyFactory(Set<ProxyCommand> proxyCommands) {
        this.proxyCommandsByPlatform = proxyCommands.stream()
                .collect(Collectors.toMap(ProxyCommand::getPlatform, Function.identity()));
    }

    public ProxyCommand getProxyCommand(Platform platform) {
        return Optional.ofNullable(proxyCommandsByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No proxy commands exists by platform: " + platform));
    }

}
