package com.kayleighrichmond.social_automation.domain.tiktok.service;

import com.kayleighrichmond.social_automation.common.command.ProxyCommand;
import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.config.AppProps;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.NoProxiesAvailableException;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.ProxyNotVerifiedException;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.common.helper.ProxyHelper;
import com.kayleighrichmond.social_automation.domain.proxy.service.ProxyService;
import com.kayleighrichmond.social_automation.domain.proxy.service.ProxyVerifier;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.UpdateProxyRequest;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import com.kayleighrichmond.social_automation.system.client.nst.NstBrowserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikTokProxyCommand implements ProxyCommand {

    private final TikTokService tikTokService;

    private final NstBrowserClient nstBrowserClient;

    private final ProxyHelper proxyHelper;

    private final ProxyService proxyService;

    private final ProxyVerifier proxyVerifier;

    private final AppProps appProps;

    @Override
    public void executeActiveProxy(String accountId) {
        TikTokAccount tikTokAccount = tikTokService.findById(accountId);
        boolean verifiedProxy = proxyVerifier.verifyProxy(tikTokAccount.getProxy(), false);

        if (!verifiedProxy) {
            log.info("Trying to find available proxy");
            try {
                Proxy proxy = executeProxyByCountryCode(tikTokAccount.getCountryCode());
                log.info("Found proxy for account {}, replacing", accountId);

                tikTokService.update(accountId, UpdateAccountRequest.builder().proxy(proxy).build());
                nstBrowserClient.updateProfileProxy(tikTokAccount.getNstProfileId(), proxy);
            } catch (NoProxiesAvailableException e) {
                log.error("No proxies available", e);
                throw new ProxyNotVerifiedException("Proxy is not verified for this account");
            }
        } else if (!tikTokAccount.getProxy().isVerified()) {
            proxyService.update(tikTokAccount.getProxy().getId(), UpdateProxyRequest.builder().verified(true).build());
        }
    }

    private Proxy executeProxyByCountryCode(String countryCode) {
        List<Proxy> proxies = proxyService.findAllByCountryCodeAndVerifiedAndAccountsLimit(countryCode, appProps.getAccountsPerProxy(), 1);
        Proxy proxy = proxyHelper.getAvailableProxiesOrRotate(proxies).stream()
                .findFirst()
                .orElseThrow(() -> new NoProxiesAvailableException("No proxies available by country code: " + countryCode));

        if (proxy.getAccountsLinked() == appProps.getAccountsPerProxy()) {
            proxyService.update(proxy.getId(), UpdateProxyRequest.builder().accountsLinked(0).build());
        }

        return proxy;
    }

    @Override
    public Platform getPlatform() {
        return Platform.TIKTOK;
    }
}
