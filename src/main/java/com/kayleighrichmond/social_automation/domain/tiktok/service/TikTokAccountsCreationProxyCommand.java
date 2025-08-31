package com.kayleighrichmond.social_automation.domain.tiktok.service;

import com.kayleighrichmond.social_automation.common.command.AccountsCreationProxyCommand;
import com.kayleighrichmond.social_automation.common.helper.ProxyHelper;
import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.config.AppProps;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.NotEnoughProxiesException;
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
        List<Proxy> availableProxies = proxyHelper.getAvailableProxiesOrRotate(verifiedProxiesByCountryCode.subList(0, createAccountsRequest.getAmount()));

        for (Proxy availableProxy : availableProxies) {
            if (availableProxy.getAccountsLinked() == appProps.getAccountsPerProxy()) {
                proxyService.update(availableProxy.getId(), UpdateProxyRequest.builder().accountsLinked(0).lastRotation(Instant.now()).build());
            }
        }

        int amountOfProxyUsage = availableProxies.stream()
                .mapToInt(value -> appProps.getAccountsPerProxy() - value.getAccountsLinked())
                .sum();
        if (amountOfProxyUsage < createAccountsRequest.getAmount()) {
            throw new NotEnoughProxiesException("Not enough proxies to create %d accounts. %s available for country %s".formatted(
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
