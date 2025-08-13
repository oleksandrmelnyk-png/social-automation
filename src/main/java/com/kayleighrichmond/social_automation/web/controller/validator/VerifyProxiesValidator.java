package com.kayleighrichmond.social_automation.web.controller.validator;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.service.proxy.ProxyService;
import com.kayleighrichmond.social_automation.service.proxy.exception.NotEnoughProxiesException;
import com.kayleighrichmond.social_automation.web.dto.account.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VerifyProxiesValidator implements Validator {

    private final ProxyService proxyService;

    @Override
    public void validate(Object target) {
        boolean verified = verifyArgument(target.getClass());
        if (!verified) {
            throw new IllegalArgumentException("CreateAccountsRequest required");
        }
        CreateAccountsRequest createAccountsRequest = (CreateAccountsRequest) target;

        proxyService.verifyAll();
        List<Proxy> proxies = proxyService.findAllVerifiedByCountryCodeAndAccountsLinked(createAccountsRequest.getCountryCode(), 5, PageRequest.of(0, createAccountsRequest.getAmount()));

        int availableProxies = proxies.stream()
                .mapToInt(value -> 5 - value.getAccountsLinked())
                .sum();

        if (availableProxies < createAccountsRequest.getAmount()) {
            throw new NotEnoughProxiesException("Not enough proxies to create %d accounts. Only %s available for country code %s".formatted(
                    createAccountsRequest.getAmount(),
                    proxies.size(),
                    createAccountsRequest.getCountryCode()
            ));
        }
    }

    @Override
    public boolean verifyArgument(Class<?> clazz) {
        return clazz.equals(CreateAccountsRequest.class);
    }
}
