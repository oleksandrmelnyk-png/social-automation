package com.kayleighrichmond.social_automation.common.helper;

import com.kayleighrichmond.social_automation.config.AppProps;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.ProxyNotVerifiedException;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.ProxyNotFoundException;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.ProxyRotationFailed;
import com.kayleighrichmond.social_automation.domain.proxy.service.ProxyVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProxyHelper {

    private final ProxyVerifier proxyVerifier;

    private final AppProps appProps;

    public void verifyOrThrow(Proxy proxy) {
        boolean verifiedProxy = proxyVerifier.verifyProxy(proxy, false);

        if (!verifiedProxy) {
            throw new ProxyNotVerifiedException("Proxy %s has not verified".formatted(proxy.getUsername()));
        }
    }

    public List<Proxy> getAvailableProxiesOrRotate(List<Proxy> verifiedProxies) {
        return verifiedProxies.stream()
                .filter(this::isProxyVerified)
                .toList();
    }

    public boolean isProxyVerified(Proxy proxy) {
        if (proxy.getAccountsLinked() < appProps.getAccountsPerProxy()) {
            return true;
        }

        if (!proxy.getRebootLink().isBlank()) {
            return rotateProxy(proxy);
        } else {
            return Instant.now().getEpochSecond() - proxy.getLastRotation().getEpochSecond() >= proxy.getAutoRotateInterval();
        }
    }

    public boolean rotateProxy(Proxy proxy) {
        try {
            proxyVerifier.changeProxyIp(proxy.getRebootLink());
            return true;
        } catch (ProxyRotationFailed | ProxyNotFoundException | IllegalArgumentException e) {
            log.warn("Exception while rotating proxy: {}", e.getMessage());
            return false;
        }
    }
}
