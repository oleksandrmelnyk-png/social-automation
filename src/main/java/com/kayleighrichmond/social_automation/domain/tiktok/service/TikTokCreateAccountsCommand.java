package com.kayleighrichmond.social_automation.domain.tiktok.service;

import com.kayleighrichmond.social_automation.common.helper.ProxyHelper;
import com.kayleighrichmond.social_automation.config.AppProps;
import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.NotEnoughProxiesException;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.tiktok.common.helper.TikTokPlaywrightHelper;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.common.command.AccountCommand;
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
public class TikTokCreateAccountsCommand implements AccountCommand {

    private final NstBrowserClient nstBrowserClient;

    private final PlaywrightInitializer playwrightInitializer;

    private final TikTokAccountCreationExceptionHandler tikTokAccountCreationExceptionHandler;

    private final TikTokService tikTokService;

    private final TikTokAccountBuilder tikTokAccountBuilder;

    private final TikTokPlaywrightHelper tikTokPlaywrightHelper;

    private final ProxyService proxyService;

    private final ProxyHelper proxyHelper;

    private final AppProps appProps;

    @Override
    public void executeAccountCreation(CreateAccountsRequest createAccountsRequest) {
        List<Proxy> proxies = getAvailableProxies(createAccountsRequest);

        List<TikTokAccount> tikTokAccountsWithProxies = proxies.stream()
                .map(tikTokAccountBuilder::buildWithProxy)
                .toList();
        List<TikTokAccount> tikTokAccounts = tikTokService.saveAll(tikTokAccountsWithProxies);

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
                } catch (Exception e) {
                    tikTokAccountCreationExceptionHandler.handle(e, tikTokAccount);
                } catch (Error e) {
                    log.error(e.getMessage());
                    tikTokService.updateAllFromCreationStatusInProgressToFailed("Unexpected server exception");
                    throw new ServerException("Something went wrong while account creation");
                }
                createdCount++;
            }
        }
    }

    private List<Proxy> getAvailableProxies(CreateAccountsRequest createAccountsRequest) {
        List<Proxy> verifiedProxiesByCountryCode = proxyService.verifyAllByCountryCode(createAccountsRequest.getCountryCode());
        List<Proxy> filteredProxiesByCountryCodeAndLimited = verifiedProxiesByCountryCode.stream()
                .filter(proxy -> proxy.getCountryCode().equals(createAccountsRequest.getCountryCode()))
                .limit(createAccountsRequest.getAmount())
                .toList();

        List<Proxy> availableProxies = proxyHelper.getAvailableProxiesOrRotate(filteredProxiesByCountryCodeAndLimited);

        for (Proxy availableProxy : availableProxies) {
            if (availableProxy.getAccountsLinked() == appProps.getAccountsPerProxy()) {
                proxyService.update(availableProxy.getId(), UpdateProxyRequest.builder().accountsLinked(0).build());
            }
        }

        int amountOfProxyUsage = proxyHelper.getAmountOfProxyUsage(availableProxies);
        if (amountOfProxyUsage < createAccountsRequest.getAmount()) {
            throw new NotEnoughProxiesException("Not enough proxies to create %d accounts. %s available for country %s".formatted(
                    createAccountsRequest.getAmount(),
                    amountOfProxyUsage,
                    createAccountsRequest.getCountryCode()
            ));
        }

        return availableProxies;
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