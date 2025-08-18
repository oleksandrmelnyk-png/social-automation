package com.kayleighrichmond.social_automation.web.controller.validator;

import com.kayleighrichmond.social_automation.domain.entity.Proxy;
import com.kayleighrichmond.social_automation.service.api.proxy.ProxyHelper;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.NotEnoughProxiesException;
import com.kayleighrichmond.social_automation.web.dto.tiktok.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SocialAccountCreationValidator implements Validator {

    private final ProxyHelper proxyHelper;

    @Override
    public void validate(Object target) {
        verifyArgument(target.getClass());
        CreateAccountsRequest createAccountsRequest = (CreateAccountsRequest) target;

        List<Proxy> verifiedProxiesByCountryCode = proxyHelper.verifyProxiesByCountryCodeAndUpdate(createAccountsRequest.getCountryCode());
        List<Proxy> availableProxies = proxyHelper.rotateAndGetAvailableProxies(verifiedProxiesByCountryCode);

        int amountOfProxyUsage = proxyHelper.getAmountOfProxyUsage(availableProxies);
        if (amountOfProxyUsage < createAccountsRequest.getAmount()) {
            throw new NotEnoughProxiesException("Not enough proxies to create %d accounts. %s available for country %s".formatted(
                    createAccountsRequest.getAmount(),
                    amountOfProxyUsage,
                    createAccountsRequest.getCountryCode()
            ));
        }
    }

    @Override
    public void verifyArgument(Class<?> clazz) throws IllegalArgumentException {
        boolean verified = clazz.equals(CreateAccountsRequest.class);
        if (!verified) {
            throw new IllegalArgumentException("CreateAccountsRequest required");
        }
    }
}
