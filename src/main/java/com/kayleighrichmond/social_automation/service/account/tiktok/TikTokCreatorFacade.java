package com.kayleighrichmond.social_automation.service.account.tiktok;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.service.mailtm.MailTmService;
import com.kayleighrichmond.social_automation.service.mailtm.exception.NoMessagesReceivedException;
import com.kayleighrichmond.social_automation.service.nst.NstBrowserClient;
import com.kayleighrichmond.social_automation.service.nst.NstBrowserService;
import com.kayleighrichmond.social_automation.service.nst.dto.CreateProfileResponse;
import com.kayleighrichmond.social_automation.service.nst.dto.StartBrowserResponse;
import com.kayleighrichmond.social_automation.service.proxy.ProxyService;
import com.kayleighrichmond.social_automation.service.randomuser.RandomUserClient;
import com.kayleighrichmond.social_automation.service.randomuser.dto.RandomUserResponse;
import com.kayleighrichmond.social_automation.type.Status;
import com.kayleighrichmond.social_automation.web.dto.account.CreateAccountsRequest;
import com.microsoft.playwright.*;
import lombok.RequiredArgsConstructor;
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

    private final RandomUserClient randomUserClient;

    private final ProxyService proxyService;

    private final TikTokService tikTokService;

    private final NstBrowserService nstBrowserService;

    @Async
    public void processAccountCreation(CreateAccountsRequest createAccountsRequest) {
        try {
            List<Proxy> proxies = proxyService.findAllVerifiedByCountryCodeAndAccountsLinked(createAccountsRequest.getCountryCode(), 5, PageRequest.of(0, createAccountsRequest.getAmount()));
            List<TikTokAccount> tikTokAccounts = createTikTokAccountsWithProxies(proxies.subList(0, createAccountsRequest.getAmount()));

            int createdCount = 0;

            for (Proxy proxy : proxies) {
                while (proxy.getAccountsLinked() < 5 && createdCount < createAccountsRequest.getAmount()) {
                    try {
                        TikTokAccount tikTokAccount = tikTokAccounts.get(createdCount++);
                        createAccountWithProxy(proxy, tikTokAccount);
                    } catch (Exception e) {
                        log.error("Error creating account with proxy {}: {}", proxy.getHost(), e.getMessage(), e);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAccountWithProxy(Proxy proxy, TikTokAccount tikTokAccount) {
        CreateProfileResponse profile = nstBrowserClient.createProfile(
                tikTokAccount.getName().getFirst() + " " + tikTokAccount.getName().getLast(),
                proxy
        );
        StartBrowserResponse startBrowserResponse = nstBrowserClient.startBrowser(profile.getData().getProfileId());

        Page page = null;
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().connectOverCDP(startBrowserResponse.getData().getWebSocketDebuggerUrl())
        ) {
            page = nstBrowserService.getContextPage(browser);
            page.navigate(TIKTOK_SIGN_UP_BROWSER_URL);

            registerAccount(tikTokAccount, page);

            proxyService.updateAccountsLinked(proxy.getId(), proxy.getAccountsLinked() + 1);
            tikTokAccount.setStatus(Status.CREATED);
            tikTokService.update(tikTokAccount);

            page.close();
        } catch (NoMessagesReceivedException e) {
            log.warn("Not received message for {}. Skipping...", profile.getData().getName());
            nstBrowserClient.deleteBrowser(profile.getData().getProfileId());
        } finally {
            if (page != null && !page.isClosed()) {
                page.close();
            }
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
            throw new RuntimeException(e);
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

    private List<TikTokAccount> createTikTokAccountsWithProxies(List<Proxy> proxies) throws InterruptedException {
        List<TikTokAccount> tikTokAccounts = new ArrayList<>();

        for (Proxy proxy : proxies) {
            RandomUserResponse.RandomResult randomUser = randomUserClient.getRandomUser();
            String password = "Qwerty1234@";
            String address = mailTmService.createAddressWithDomainOncePerSecond(randomUser.getEmail(), password);

            TikTokAccount tikTokAccount = TikTokAccount.builder()
                    .email(address)
                    .password(password)
                    .name(randomUser.getName())
                    .proxy(proxy)
                    .status(Status.IN_PROGRESS)
                    .dob(randomUser.getDob())
                    .build();

            tikTokAccounts.add(tikTokAccount);
            tikTokService.save(tikTokAccount);
        }

        return tikTokAccounts;
    }
}