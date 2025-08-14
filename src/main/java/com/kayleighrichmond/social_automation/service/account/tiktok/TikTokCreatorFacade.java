package com.kayleighrichmond.social_automation.service.account.tiktok;

import com.kayleighrichmond.social_automation.exception.ServerException;
import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.service.account.tiktok.builder.TikTokAccountBuilder;
import com.kayleighrichmond.social_automation.service.mailtm.MailTmService;
import com.kayleighrichmond.social_automation.service.mailtm.exception.NoMessagesReceivedException;
import com.kayleighrichmond.social_automation.service.nst.NstBrowserClient;
import com.kayleighrichmond.social_automation.service.nst.dto.CreateProfileResponse;
import com.kayleighrichmond.social_automation.service.nst.exception.NstBrowserException;
import com.kayleighrichmond.social_automation.service.playwright.PlaywrightService;
import com.kayleighrichmond.social_automation.service.playwright.dto.PlaywrightDto;
import com.kayleighrichmond.social_automation.service.proxy.ProxyService;
import com.kayleighrichmond.social_automation.type.Status;
import com.kayleighrichmond.social_automation.web.dto.account.CreateAccountsRequest;
import com.microsoft.playwright.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kayleighrichmond.social_automation.service.account.tiktok.TikTokConstants.TIKTOK_SIGN_UP_BROWSER_URL;
import static com.kayleighrichmond.social_automation.service.account.tiktok.TikTokSelectors.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokCreatorFacade {

    private final MailTmService mailTmService;

    private final NstBrowserClient nstBrowserClient;

    private final ProxyService proxyService;

    private final TikTokService tikTokService;

    private final TikTokAccountBuilder tikTokAccountBuilder;

    private final PlaywrightService playwrightService;

    @Async
    public void processAccountCreation(CreateAccountsRequest createAccountsRequest) {
        List<Proxy> proxies = proxyService.findAllVerifiedByCountryCodeAndAccountsLinked(createAccountsRequest.getCountryCode(), 5, PageRequest.of(0, createAccountsRequest.getAmount()));
        List<TikTokAccount> tikTokAccounts = createTikTokAccountsWithProxies(proxies.subList(0, createAccountsRequest.getAmount()));

        int createdCount = 0;

        for (Proxy proxy : proxies) {
            while (proxy.getAccountsLinked() < 5 && createdCount < createAccountsRequest.getAmount()) {
                try {
                    TikTokAccount tikTokAccount = tikTokAccounts.get(createdCount++);
                    createAccountWithProxy(proxy, tikTokAccount);
                }
                catch (NstBrowserException e) {
                    log.error("NstBrowserException: {}", e.getMessage());
                    tikTokService.updateAllFromInProgressToFailed();
                    throw new NstBrowserException("Nst browser exception occurred");
                } catch (Exception e) {
                    log.error("Exception: {}", e.getMessage());
                    tikTokService.updateAllFromInProgressToFailed();
                    throw new ServerException("Something went wrong with account registering");
                }
            }
        }
    }

    @SneakyThrows
    private void createAccountWithProxy(Proxy proxy, TikTokAccount tikTokAccount) {
        CreateProfileResponse profile = nstBrowserClient.createProfile(tikTokAccount.getName().getFirst() + " " + tikTokAccount.getName().getLast(), proxy);
        PlaywrightDto playwrightDto =  playwrightService.initPlaywright(profile.getData().getProfileId());
        Page page = playwrightDto.getPage();

        try {
            page.navigate(TIKTOK_SIGN_UP_BROWSER_URL);
            registerAccount(tikTokAccount, page);

            proxyService.updateAccountsLinked(proxy.getId(), proxy.getAccountsLinked() + 1);
            tikTokAccount.setStatus(Status.CREATED);
            tikTokService.update(tikTokAccount);

            log.info("TikTok account successfully created by email {}", tikTokAccount.getEmail());
        } catch (NoMessagesReceivedException e) {
            log.error("NoMessagesReceivedException: {}", e.getMessage());
            nstBrowserClient.deleteBrowser(profile.getData().getProfileId());
            throw new NoMessagesReceivedException("Didn't receive message from mail service for " + tikTokAccount.getEmail());
        } finally {
            playwrightDto.getAutoCloseables().forEach(AutoCloseable::close);
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

            page.waitForSelector(RESEND_CODE_TIMEOUT);

            Thread.sleep(1200 + (long)(Math.random() * 1600));
            String codeFromGeneratedEmail = mailTmService.getCodeFromGeneratedEmail(tikTokAccount.getEmail(), tikTokAccount.getPassword());

            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.fill(CODE_INPUT, codeFromGeneratedEmail);

            Thread.sleep(random.nextInt(1000));
            page.click(NEXT_BUTTON);

            page.waitForSelector(SIGN_UP_BUTTON);
            Thread.sleep(1200 + (long)(Math.random() * 1600));
            page.click(SKIP);

            Thread.sleep(1200 + (long)(Math.random() * 1600));
            waitForAddAndClickOrSkip(page);

        } catch (InterruptedException e) {
            log.error("InterruptedException: {}", e.getMessage());
            throw new ServerException("Something went wrong with account registering");
        }
    }

    private void waitForAddAndClickOrSkip(Page page) {
        Locator locator = page.locator(SELECT_ADD);
        boolean appeared;

        try {
            locator.waitFor(new Locator.WaitForOptions().setTimeout(7000));
            appeared = locator.isVisible();
        } catch (PlaywrightException e) {
            appeared = false;
        }

        if (appeared) {
            locator.click();
        }
    }

    private List<TikTokAccount> createTikTokAccountsWithProxies(List<Proxy> proxies) {
        List<TikTokAccount> tikTokAccounts = new ArrayList<>();

        for (Proxy proxy : proxies) {
            TikTokAccount tikTokAccount = tikTokAccountBuilder.buildWithProxy(proxy);

            tikTokAccounts.add(tikTokAccount);
            tikTokService.save(tikTokAccount);
        }

        return tikTokAccounts;
    }
}