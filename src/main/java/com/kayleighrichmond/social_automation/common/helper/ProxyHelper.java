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
        Predicate<Proxy> verifiedProxyPredicate = proxy -> {
            if (proxy.getAccountsLinked() < appProps.getAccountsPerProxy()) {
                return true;
            }
            return rotateProxyAndResetAccountsLinked(proxy);
        };

        return verifiedProxies.stream()
                .filter(verifiedProxyPredicate)
                .toList();
    }

    public int getAmountOfProxyUsage(List<Proxy> proxies) {
        return proxies.stream()
                .mapToInt(value -> appProps.getAccountsPerProxy() - value.getAccountsLinked())
                .sum();
    }

    public boolean rotateProxyAndResetAccountsLinked(Proxy proxy) {
        try {
            proxyVerifier.changeProxyIp(proxy.getRebootLink());
            return true;
        } catch (ProxyRotationFailed | ProxyNotFoundException | IllegalArgumentException e) {
            log.warn("Exception while rotating proxy: {}", e.getMessage());
            return false;
        }
    }
}
