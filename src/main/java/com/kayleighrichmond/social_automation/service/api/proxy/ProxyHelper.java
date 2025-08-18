package com.kayleighrichmond.social_automation.service.api.proxy;

import com.kayleighrichmond.social_automation.config.AppProps;
import com.kayleighrichmond.social_automation.domain.entity.Proxy;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.ProxyNotFoundException;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.ProxyRotationFailed;
import com.kayleighrichmond.social_automation.web.dto.proxy.UpdateProxyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProxyHelper {

    private final ProxyService proxyService;

    private final ProxyVerifier proxyVerifier;

    private final AppProps appProps;

    public List<Proxy> verifyProxiesByCountryCodeAndUpdate(String countryCode) {
        List<Proxy> proxies = proxyService.findAllByCountryCode(countryCode);
        List<Proxy> verifiedProxies = new ArrayList<>();

        for (Proxy proxy : proxies) {
            boolean verifiedProxy = proxyVerifier.verifyProxy(proxy, false);
            if (verifiedProxy) {
                verifiedProxies.add(proxy);

                if (!proxy.isVerified()) {
                    proxyService.update(proxy.getId(), UpdateProxyRequest.builder().verified(true).build());
                }
            } else {
                proxyService.update(proxy.getId(), UpdateProxyRequest.builder().verified(false).build());
            }
        }

        return verifiedProxies;
    }

    public List<Proxy> rotateAndGetAvailableProxies(List<Proxy> verifiedProxies) {
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
            proxyService.update(proxy.getId(), UpdateProxyRequest.builder().accountsLinked(0).build());
            proxyVerifier.changeProxyIp(proxy.getRebootLink());

            return true;
        } catch (ProxyRotationFailed | ProxyNotFoundException | IllegalArgumentException e) {
            log.warn("Exception while rotating proxy: {}", e.getMessage());
            return false;
        }
    }
}
