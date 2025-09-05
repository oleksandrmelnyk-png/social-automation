package com.kayleighrichmond.social_automation.domain.tiktok.service.proxy;

import com.kayleighrichmond.social_automation.common.command.AccountsCreationProxyCommand;
import com.kayleighrichmond.social_automation.common.helper.ProxyHelper;
import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.config.AppProps;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.ProxyNotAvailableException;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.proxy.service.ProxyService;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.UpdateProxyRequest;
import com.kayleighrichmond.social_automation.system.controller.dto.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokAccountsCreationProxyCommand implements AccountsCreationProxyCommand {

    private final ProxyHelper proxyHelper;

    private final ProxyService proxyService;

    private final AppProps appProps;

    @Override
    public void executeAvailableProxies(CreateAccountsRequest createAccountsRequest) {
        List<Proxy> verifiedProxiesByCountryCode = proxyService.verifyAllByCountryCode(createAccountsRequest.getCountryCode());
        if (verifiedProxiesByCountryCode.isEmpty()) {
            throw new ProxyNotAvailableException("No verified proxies are available to create %d accounts for country %s".formatted(
                    createAccountsRequest.getAmount(),
                    createAccountsRequest.getCountryCode()
            ));
        }

        List<Proxy> availableProxies = proxyHelper.getAvailableProxiesOrRotate(verifiedProxiesByCountryCode);
        if (availableProxies.isEmpty()) {
            throw new ProxyNotAvailableException("No available proxies to create %d accounts for country %s".formatted(
                    createAccountsRequest.getAmount(),
                    createAccountsRequest.getCountryCode()
            ));
        }

        for (Proxy availableProxy : availableProxies.subList(0, createAccountsRequest.getAmount())) {
            if (availableProxy.getAccountsLinked() == appProps.getAccountsPerProxy()) {
                proxyService.update(availableProxy.getId(), UpdateProxyRequest.builder().accountsLinked(0).lastRotation(Instant.now()).build());
            }
        }

        int amountOfProxyUsage = availableProxies.stream()
                .mapToInt(value -> appProps.getAccountsPerProxy() - value.getAccountsLinked())
                .sum();
        if (amountOfProxyUsage < createAccountsRequest.getAmount()) {
            throw new ProxyNotAvailableException("Not enough proxies to create %d accounts. %s available for country %s".formatted(
                    createAccountsRequest.getAmount(),
                    amountOfProxyUsage,
                    createAccountsRequest.getCountryCode()
            ));
        }
    }

    @Override
    public Platform getPlatform() {
        return Platform.TIKTOK;
    }
}
