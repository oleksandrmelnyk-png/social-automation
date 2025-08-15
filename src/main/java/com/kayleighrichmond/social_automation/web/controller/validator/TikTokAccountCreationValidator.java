package com.kayleighrichmond.social_automation.web.controller.validator;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.service.account.tiktok.TikTokService;
import com.kayleighrichmond.social_automation.service.proxy.ProxyHelper;
import com.kayleighrichmond.social_automation.service.proxy.exception.NotEnoughProxiesException;
import com.kayleighrichmond.social_automation.type.Status;
import com.kayleighrichmond.social_automation.web.dto.tiktok.CreateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TikTokAccountCreationValidator implements Validator {

    private final TikTokService tikTokService;

    private final ProxyHelper proxyHelper;

    @Override
    public void validate(Object target) {
        verifyArgument(target.getClass());
        CreateAccountRequest createAccountRequest = (CreateAccountRequest) target;

        tikTokService.throwIfAccountsInProgressExists(Status.IN_PROGRESS);

        List<Proxy> verifiedProxiesByCountryCode = proxyHelper.verifyProxiesByCountryCodeAndUpdate(createAccountRequest.getCountryCode());
        List<Proxy> availableProxies = proxyHelper.rotateAndGetAvailableProxies(verifiedProxiesByCountryCode);

        int amountOfProxyUsage = proxyHelper.getAmountOfProxyUsage(availableProxies);
        if (amountOfProxyUsage < createAccountRequest.getAmount()) {
            throw new NotEnoughProxiesException("Not enough proxies to create %d accounts. %s available for country %s".formatted(
                    createAccountRequest.getAmount(),
                    amountOfProxyUsage,
                    createAccountRequest.getCountryCode()
            ));
        }
    }

    @Override
    public void verifyArgument(Class<?> clazz) {
        boolean verified = clazz.equals(CreateAccountRequest.class);
        if (!verified) {
            throw new IllegalArgumentException("CreateAccountsRequest required");
        }
    }
}
