package com.kayleighrichmond.social_automation.domain.tiktok.service;

import com.kayleighrichmond.social_automation.config.AppProps;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.tiktok.common.helper.TikTokPlaywrightHelper;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.common.command.AccountsCreationCommand;
import com.kayleighrichmond.social_automation.domain.tiktok.common.builder.TikTokAccountBuilder;
import com.kayleighrichmond.social_automation.domain.tiktok.common.exception.TikTokAccountCreationExceptionHandler;
import com.kayleighrichmond.social_automation.system.client.nst.NstBrowserClient;
import com.kayleighrichmond.social_automation.system.client.nst.dto.CreateProfileResponse;
import com.kayleighrichmond.social_automation.config.PlaywrightInitializer;
import com.kayleighrichmond.social_automation.system.client.playwright.dto.PlaywrightDto;
import com.kayleighrichmond.social_automation.domain.proxy.service.ProxyService;
import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.common.type.Status;
import com.kayleighrichmond.social_automation.system.controller.dto.CreateAccountsRequest;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.UpdateProxyRequest;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import com.microsoft.playwright.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokAccountsCreationCommand implements AccountsCreationCommand {

    private final NstBrowserClient nstBrowserClient;

    private final PlaywrightInitializer playwrightInitializer;

    private final TikTokAccountCreationExceptionHandler tikTokAccountCreationExceptionHandler;

    private final TikTokService tikTokService;

    private final TikTokAccountBuilder tikTokAccountBuilder;

    private final TikTokPlaywrightHelper tikTokPlaywrightHelper;

    private final ProxyService proxyService;

    private final AppProps appProps;

    @Override
    public void executeAccountCreation(CreateAccountsRequest createAccountsRequest) {
        List<Proxy> proxies = proxyService.findAllByCountryCodeAndVerifiedAndLimit(createAccountsRequest.getCountryCode(), true, createAccountsRequest.getAmount());

        List<TikTokAccount> tikTokAccountsWithProxies = proxies.stream()
                .map(tikTokAccountBuilder::buildWithProxy)
                .toList();
        List<TikTokAccount> tikTokAccounts = tikTokService.saveAllOrThrow(tikTokAccountsWithProxies);

        int createdCount = 0;
        for (Proxy proxy : proxies) {
            TikTokAccount tikTokAccount = tikTokAccounts.get(createdCount);
            while (proxy.getAccountsLinked() < appProps.getAccountsPerProxy() && createdCount < createAccountsRequest.getAmount()) {
                try {
                    CreateProfileResponse createProfileResponse = initializeNstAndStartAccountCreation(proxy, tikTokAccount);

                    UpdateProxyRequest updateProxyRequest = UpdateProxyRequest.builder()
                            .accountsLinked(proxy.getAccountsLinked() + 1)
                            .build();
                    proxyService.update(proxy.getId(), updateProxyRequest);

                    UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                            .status(Status.CREATED)
                            .executionMessage(null)
                            .nstProfileId(createProfileResponse.getData().getProfileId())
                            .build();
                    tikTokService.update(tikTokAccount.getId(), updateAccountRequest);

                    log.info("TikTok account successfully created by email {}", tikTokAccount.getEmail());
                } catch (Throwable e) {
                    tikTokAccountCreationExceptionHandler.handle(e, tikTokAccount);
                }
                createdCount++;
            }
        }
    }

    private CreateProfileResponse initializeNstAndStartAccountCreation(Proxy proxy, TikTokAccount tikTokAccount) {
        CreateProfileResponse createProfileResponse = nstBrowserClient.createProfile(tikTokAccount.getName().getFirst() + " " + tikTokAccount.getName().getLast(), proxy);
        PlaywrightDto playwrightDto = playwrightInitializer.initPlaywright(createProfileResponse.getData().getProfileId());

        Page page = playwrightDto.getPage();

        try {
            tikTokPlaywrightHelper.processSignUp(page, tikTokAccount);
        } finally {
            playwrightDto.getAutoCloseables().forEach(ac -> {
                try {
                    ac.close();
                } catch (Exception e) {
                    log.error("Failed to close resource", e);
                }
            });
        }

        return createProfileResponse;
    }

    @Override
    public Platform getPlatform() {
        return Platform.TIKTOK;
    }
}