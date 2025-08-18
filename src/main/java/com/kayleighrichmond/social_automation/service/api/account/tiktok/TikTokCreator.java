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
import com.kayleighrichmond.social_automation.service.client.playwright.PlaywrightService;
import com.kayleighrichmond.social_automation.service.client.playwright.dto.PlaywrightDto;
import com.kayleighrichmond.social_automation.service.api.proxy.ProxyService;
import com.kayleighrichmond.social_automation.domain.type.Platform;
import com.kayleighrichmond.social_automation.domain.type.Status;
import com.kayleighrichmond.social_automation.web.dto.tiktok.CreateAccountsRequest;
import com.kayleighrichmond.social_automation.web.dto.proxy.UpdateProxyRequest;
import com.kayleighrichmond.social_automation.web.dto.tiktok.UpdateAccountRequest;
import com.microsoft.playwright.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

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

    private final PlaywrightService playwrightService;

    private final AppProps appProps;

    private static final String TIKTOK_SIGN_UP_BROWSER_URL = "https://www.google.com/search?q=tiktok+sign+up";

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
                    createAccountWithProxy(proxy, tikTokAccount);
                } catch (Exception e) {
                    tikTokAccountCreationExceptionHandler.handle(e, tikTokAccount);
                } catch (Error e) {
                    log.error("Exception: {}", e.getMessage());
                    tikTokService.updateAllFromInProgressToFailed("Unexpected server exception");
                    throw new ServerException(e.getMessage());
                }
                createdCount++;
            }
        }
    }

    private void createAccountWithProxy(Proxy proxy, TikTokAccount tikTokAccount) {
        CreateProfileResponse profile = nstBrowserClient.createProfile(tikTokAccount.getName().getFirst() + " " + tikTokAccount.getName().getLast(), proxy);
        PlaywrightDto playwrightDto = playwrightInitializer.initPlaywright(profile.getData().getProfileId());

        Page page = playwrightDto.getPage();

        try {
            log.info("Opening browser...");
            page.navigate(TIKTOK_SIGN_UP_BROWSER_URL);

            log.info("Starting account creation...");
            registerAccount(tikTokAccount, page);

            proxyService.update(proxy.getId(), UpdateProxyRequest.builder().accountsLinked(proxy.getAccountsLinked() + 1).build());
            tikTokService.update(tikTokAccount.getId(), UpdateAccountRequest.builder().status(Status.CREATED).build());

            log.info("TikTok account successfully created by email {}", tikTokAccount.getEmail());
        } finally {
            playwrightDto.getAutoCloseables().forEach(ac -> {
                try {
                    ac.close();
                } catch (Exception e) {
                    log.error("Failed to close resource", e);
                }
            });
        }
    }

    private void registerAccount(TikTokAccount tikTokAccount, Page page) {
        Random random = new Random();
        LocalDate dotDate = LocalDate.parse(tikTokAccount.getDob().getDate().substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            page.waitForSelector(HOME_SIGN_UP);
            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.click(HOME_SIGN_UP);

            page.waitForSelector(LANGUAGE_SELECT);
            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.selectOption(LANGUAGE_SELECT, "en");

            page.waitForSelector(USE_PHONE_OR_EMAIL);
            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.click(USE_PHONE_OR_EMAIL);

            page.waitForSelector(SIGN_UP_WITH_EMAIL);
            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.click(SIGN_UP_WITH_EMAIL);

            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.click(MONTH_DIV);
            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.click(selectMonth(Month.of(dotDate.getMonthValue())));

            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.click(DAY_DIV);
            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.click(selectDay(dotDate.getDayOfMonth()));

            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.click(YEAR_DIV);
            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.click(selectYear(Math.min(dotDate.getYear(), 2007)));

            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.fill(EMAIL_INPUT, tikTokAccount.getEmail());

            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.fill(PASSWORD_INPUT, tikTokAccount.getPassword());

            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.focus(SEND_CODE_BUTTON);
            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.click(SEND_CODE_BUTTON);

            playwrightService.waitForSelectorAndAct(page, CAPTCHA_DIV, locator -> {
                throw new CaptchaException("Captcha appeared");
            });

            page.waitForSelector(RESEND_CODE_TIMEOUT);

            Thread.sleep(1200 + (long)(Math.random() * 1600));
            String codeFromGeneratedEmail = mailTmService.getCodeFromGeneratedEmail(tikTokAccount.getEmail(), tikTokAccount.getPassword());

            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.fill(CODE_INPUT, codeFromGeneratedEmail);

            Thread.sleep(random.nextInt(1000));
            page.click(NEXT_BUTTON);

            page.waitForSelector(SIGN_UP_BUTTON);
            Thread.sleep(1200 + (long)(Math.random() * 1600));

            page.waitForSelector(USERNAME_INPUT);
            page.fill(USERNAME_INPUT, tikTokAccount.getUsername());
            Thread.sleep(1200 + (long)(Math.random() * 1600));

            page.waitForSelector(SIGN_UP_BUTTON);
            page.click(SIGN_UP_BUTTON);
            Thread.sleep(1200 + (long)(Math.random() * 1600));

            playwrightService.waitForSelectorAndAct(page, SELECT_ADD, Locator::click);
            Thread.sleep(1200 + (long)(Math.random() * 1600));

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