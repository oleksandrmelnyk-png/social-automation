package com.kayleighrichmond.social_automation.web.controller.validator;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.service.account.tiktok.TikTokService;
import com.kayleighrichmond.social_automation.service.proxy.ProxyService;
import com.kayleighrichmond.social_automation.service.proxy.ProxyVerifier;
import com.kayleighrichmond.social_automation.service.proxy.exception.NotEnoughProxiesException;
import com.kayleighrichmond.social_automation.type.Status;
import com.kayleighrichmond.social_automation.web.dto.tiktok.CreateAccountRequest;
import com.kayleighrichmond.social_automation.web.dto.proxy.UpdateProxyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TikTokAccountCreationValidator implements Validator {

    private final TikTokService tikTokService;

    private final ProxyService proxyService;

    private final ProxyVerifier proxyVerifier;

    @Override
    public void validate(Object target) {
        tikTokService.throwIfAccountsInProgressExists(Status.IN_PROGRESS);

        boolean verified = verifyArgument(target.getClass());
        if (!verified) {
            throw new IllegalArgumentException("CreateAccountsRequest required");
        }
        CreateAccountRequest createAccountRequest = (CreateAccountRequest) target;

        List<Proxy> proxies = verifyProxies().stream()
                .filter(proxy -> proxy.getAccountsLinked() < 5)
                .limit(createAccountRequest.getAmount())
                .toList();

        int availableProxies = proxies.stream()
                .mapToInt(value -> 5 - value.getAccountsLinked())
                .sum();

        if (availableProxies < createAccountRequest.getAmount()) {
            throw new NotEnoughProxiesException("Not enough proxies to create %d accounts. %s available for country %s".formatted(
                    createAccountRequest.getAmount(),
                    proxies.size(),
                    createAccountRequest.getCountryCode()
            ));
        }
    }

    private List<Proxy> verifyProxies() {
        List<Proxy> proxies = proxyService.findAll();
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

    @Override
    public boolean verifyArgument(Class<?> clazz) {
        return clazz.equals(CreateAccountRequest.class);
    }
}
