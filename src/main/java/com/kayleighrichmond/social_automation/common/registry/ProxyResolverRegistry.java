package com.kayleighrichmond.social_automation.common.registry;

import com.kayleighrichmond.social_automation.common.ProxyResolver;
import com.kayleighrichmond.social_automation.common.type.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProxyResolverRegistry {

    private final Map<Platform, ProxyResolver> proxyResolversByPlatform;

    public ProxyResolverRegistry(Set<ProxyResolver> proxyResolvers) {
        this.proxyResolversByPlatform = proxyResolvers.stream()
                .collect(Collectors.toMap(ProxyResolver::getPlatform, Function.identity()));
    }

    public ProxyResolver getProxyResolver(Platform platform) {
        return Optional.ofNullable(proxyResolversByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No proxy resolvers exists by platform: " + platform));
    }

}
