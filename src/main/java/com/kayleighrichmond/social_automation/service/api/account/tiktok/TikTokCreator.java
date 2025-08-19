package com.kayleighrichmond.social_automation.service.api.account.tiktok;

import com.kayleighrichmond.social_automation.config.AppProps;
import com.kayleighrichmond.social_automation.exception.ServerException;
import com.kayleighrichmond.social_automation.domain.entity.Proxy;
import com.kayleighrichmond.social_automation.domain.entity.account.TikTokAccount;
import com.kayleighrichmond.social_automation.service.api.account.AccountCreator;
import com.kayleighrichmond.social_automation.service.api.account.exception.CaptchaException;
import com.kayleighrichmond.social_automation.service.api.account.tiktok.builder.TikTokAccountBuilder;
import com.kayleighrichmond.social_automation.service.api.account.tiktok.exception.TikTokAccountCreationExceptionHandler;
import com.kayleighrichmond.social_automation.service.client.mailtm.MailTmService;
import com.kayleighrichmond.social_automation.service.client.nst.NstBrowserClient;
import com.kayleighrichmond.social_automation.service.client.nst.dto.CreateProfileResponse;
import com.kayleighrichmond.social_automation.service.http.PlaywrightInitializer;
import com.kayleighrichmond.social_automation.service.client.playwright.PlaywrightHelper;
import com.kayleighrichmond.social_automation.service.client.playwright.dto.PlaywrightDto;
import com.kayleighrichmond.social_automation.service.api.proxy.ProxyService;
import com.kayleighrichmond.social_automation.domain.type.Platform;
import com.kayleighrichmond.social_automation.domain.type.Status;
import com.kayleighrichmond.social_automation.web.controller.social.dto.CreateAccountsRequest;
import com.kayleighrichmond.social_automation.web.controller.proxy.dto.UpdateProxyRequest;
import com.kayleighrichmond.social_automation.web.controller.tiktok.dto.UpdateAccountRequest;
import com.microsoft.playwright.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import static com.kayleighrichmond.social_automation.service.api.account.WaitHelper.waitRandomlyInRange;
import static com.kayleighrichmond.social_automation.service.api.account.tiktok.TikTokConstants.TIKTOK_SIGN_UP_BROWSER_URL;
import static com.kayleighrichmond.social_automation.service.api.account.tiktok.TikTokSelectors.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokCreator implements AccountCreator {

    private final TikTokAccountCreationExceptionHandler tikTokAccountCreationExceptionHandler;

    private final MailTmService mailTmService;

    private final NstBrowserClient nstBrowserClient;

    private final ProxyService proxyService;

    private final TikTokService tikTokService;

    private final TikTokAccountBuilder tikTokAccountBuilder;

    private final PlaywrightInitializer playwrightInitializer;

    private final PlaywrightHelper playwrightHelper;

    private final AppProps appProps;

    @Override
    public void processAccountCreation(CreateAccountsRequest createAccountsRequest) {
        List<Proxy> proxies = proxyService.findAllVerifiedByCountryCodeAndAccountsLinked(createAccountsRequest.getCountryCode(), appProps.getAccountsPerProxy(), createAccountsRequest.getAmount());

        List<TikTokAccount> tikTokAccountsWithProxies = proxies.stream().map(tikTokAccountBuilder::buildWithProxy).toList();
        List<TikTokAccount> savedTikTokAccounts = tikTokService.saveAll(tikTokAccountsWithProxies);

        int createdCount = 0;
        for (Proxy proxy : proxies) {
            TikTokAccount tikTokAccount = savedTikTokAccounts.get(createdCount);
            while (proxy.getAccountsLinked() < appProps.getAccountsPerProxy() && createdCount < createAccountsRequest.getAmount()) {
                try {
                    CreateProfileResponse createProfileResponse = initializeNstAndStartAccountCreation(proxy, tikTokAccount);

                    UpdateProxyRequest updateProxyRequest = UpdateProxyRequest.builder()
                            .accountsLinked(proxy.getAccountsLinked() + 1)
                            .build();
                    proxyService.update(proxy.getId(), updateProxyRequest);

                    UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                            .status(Status.CREATED)
                            .nstProfileId(createProfileResponse.getData().getProfileId())
                            .build();
                    tikTokService.update(tikTokAccount.getId(), updateAccountRequest);

                    log.info("TikTok account successfully created by email {}", tikTokAccount.getEmail());
                } catch (Exception e) {
                    tikTokAccountCreationExceptionHandler.handle(e, tikTokAccount);
                } catch (Error e) {
                    log.error(e.getMessage());
                    tikTokService.updateAllFromInProgressToFailed("Unexpected server exception");
                    throw new ServerException(e.getMessage());
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
            processAccountRegistration(tikTokAccount, page);
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

    private void processAccountRegistration(TikTokAccount tikTokAccount, Page page) {
        Random random = new Random();
        LocalDate dotDate = LocalDate.parse(tikTokAccount.getDob().getDate().substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            log.info("Opening browser");
            page.navigate(TIKTOK_SIGN_UP_BROWSER_URL);

            page.waitForSelector(HOME_SIGN_UP);
            waitRandomlyInRange(1000, 2000);
            page.click(HOME_SIGN_UP);
            log.info("Starting account creation");

            page.waitForSelector(LANGUAGE_SELECT);
            waitRandomlyInRange(1100, 1900);
            page.selectOption(LANGUAGE_SELECT, "en");

            page.waitForSelector(SIGN_UP_USE_PHONE_OR_EMAIL);
            waitRandomlyInRange(1200, 2300);
            page.click(SIGN_UP_USE_PHONE_OR_EMAIL);

            page.waitForSelector(SIGN_UP_WITH_EMAIL);
            waitRandomlyInRange(1000, 1700);
            page.click(SIGN_UP_WITH_EMAIL);

            waitRandomlyInRange(1200, 1600);
            page.click(MONTH_DIV);
            waitRandomlyInRange(1200, 2000);
            page.click(selectMonth(Month.of(dotDate.getMonthValue())));

            waitRandomlyInRange(1000, 1500);
            page.click(DAY_DIV);
            waitRandomlyInRange(1100, 1900);
            page.click(selectDay(dotDate.getDayOfMonth()));

            waitRandomlyInRange(1200, 1800);
            page.click(YEAR_DIV);
            waitRandomlyInRange(1000, 1300);
            page.click(selectYear(Math.min(dotDate.getYear(), 2007)));

            waitRandomlyInRange(1000, 1500);
            page.fill(SIGN_UP_EMAIL_INPUT, tikTokAccount.getEmail());

            waitRandomlyInRange(1700, 2500);
            page.fill(PASSWORD_INPUT, tikTokAccount.getPassword());

            waitRandomlyInRange(900, 1700);
            page.focus(SEND_CODE_BUTTON);
            waitRandomlyInRange(1300, 1700);
            page.click(SEND_CODE_BUTTON);

            playwrightHelper.waitForSelectorAndAct(page, CAPTCHA_DIV, locator -> {
                throw new CaptchaException("Captcha appeared");
            });

            page.waitForSelector(RESEND_CODE_TIMEOUT);

            waitRandomlyInRange(1200, 1700);
            String codeFromGeneratedEmail = mailTmService.getCodeFromGeneratedEmail(tikTokAccount.getEmail(), tikTokAccount.getPassword());

            waitRandomlyInRange(1300, 1900);
            page.fill(CODE_INPUT, codeFromGeneratedEmail);

            waitRandomlyInRange(1800, 2100);
            page.click(NEXT_BUTTON);

            page.waitForSelector(SIGN_UP_BUTTON);
            waitRandomlyInRange(1000, 1500);

            page.waitForSelector(USERNAME_INPUT);
            page.fill(USERNAME_INPUT, tikTokAccount.getUsername());
            waitRandomlyInRange(1000, 1700);

            page.waitForSelector(SIGN_UP_BUTTON);
            page.click(SIGN_UP_BUTTON);
            waitRandomlyInRange(1000, 1500);

            playwrightHelper.waitForSelectorAndAct(page, SELECT_ADD, Locator::click);
            waitRandomlyInRange(1000, 1400);

        } catch (InterruptedException e) {
            log.error("InterruptedException: {}", e.getMessage());
            throw new ServerException("Something went wrong with account registering");
        }
    }

    @Override
    public Platform getPlatform() {
        return Platform.TIKTOK;
    }
}