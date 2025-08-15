package com.kayleighrichmond.social_automation.service.proxy;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.service.proxy.exception.ProxyNotFoundException;
import com.kayleighrichmond.social_automation.service.proxy.exception.ProxyRotationFailed;
import com.kayleighrichmond.social_automation.web.dto.proxy.UpdateProxyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class ProxyHelper {

    private final ProxyService proxyService;

    private final ProxyVerifier proxyVerifier;

    @Value("${proxy.accounts-per-proxy}")
    private int ACCOUNTS_PER_PROXY;

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
            if (proxy.getAccountsLinked() < ACCOUNTS_PER_PROXY) {
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
                .mapToInt(value -> ACCOUNTS_PER_PROXY - value.getAccountsLinked())
                .sum();
    }

    private boolean rotateProxyAndResetAccountsLinked(Proxy proxy) {
        try {
            proxyVerifier.changeProxyIp(proxy.getRebootLink());
            proxyService.update(proxy.getId(), UpdateProxyRequest.builder().accountsLinked(0).build());

            return true;
        } catch (ProxyRotationFailed | ProxyNotFoundException | IllegalArgumentException e) {
            return false;
        }
    }
}
