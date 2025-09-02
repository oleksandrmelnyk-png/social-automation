package com.kayleighrichmond.social_automation.domain.tiktok.common.helper;

import com.kayleighrichmond.social_automation.common.exception.BrowserCaptchaException;
import com.kayleighrichmond.social_automation.common.exception.CaptchaException;
import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.domain.tiktok.common.exception.SendCodeButtonNotWorkedException;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.system.client.mailtm.MailTmService;
import com.kayleighrichmond.social_automation.system.client.playwright.PlaywrightHelper;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static com.kayleighrichmond.social_automation.common.constants.MainSelectors.BROWSER_CAPTCHA;
import static com.kayleighrichmond.social_automation.common.helper.WaitHelper.waitRandomlyInRange;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokConstants.TIKTOK_SIGN_IN_BROWSER_URL;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokConstants.TIKTOK_SIGN_UP_BROWSER_URL;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokSelectors.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokPlaywrightHelper {

    private final PlaywrightHelper playwrightHelper;

    private final MailTmService mailTmService;

    public void processLogIn(Page page, TikTokAccount tikTokAccount) throws InterruptedException {
        log.info("Logging in");

        page.navigate(TIKTOK_SIGN_IN_BROWSER_URL, new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));

        page.waitForSelector(HOME_L0G_IN);
        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.click(HOME_L0G_IN);

        page.waitForSelector(LANGUAGE_SELECT);
        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.selectOption(LANGUAGE_SELECT, "en");

        page.waitForSelector(LOG_IN_USE_PHONE_OR_EMAIL_OR_USERNAME);
        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.click(LOG_IN_USE_PHONE_OR_EMAIL_OR_USERNAME);

        page.waitForSelector(LOG_IN_WITH_EMAIL_OR_USERNAME);
        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.click(LOG_IN_WITH_EMAIL_OR_USERNAME);

        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.fill(LOG_IN_EMAIL_INPUT, tikTokAccount.getEmail());

        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.fill(PASSWORD_INPUT, tikTokAccount.getPassword());

        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.click(LOG_IN_BUTTON);

        page.navigate(TIKTOK_SIGN_IN_BROWSER_URL, new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));

        page.waitForSelector(HOME_L0G_IN);
        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.click(HOME_L0G_IN);

        page.waitForSelector(LANGUAGE_SELECT);
        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.selectOption(LANGUAGE_SELECT, "en");

        page.waitForSelector(LOG_IN_USE_PHONE_OR_EMAIL_OR_USERNAME);
        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.click(LOG_IN_USE_PHONE_OR_EMAIL_OR_USERNAME);

        page.waitForSelector(LOG_IN_WITH_EMAIL_OR_USERNAME);
        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.click(LOG_IN_WITH_EMAIL_OR_USERNAME);

        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.fill(LOG_IN_EMAIL_INPUT, tikTokAccount.getEmail());

        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.fill(PASSWORD_INPUT, tikTokAccount.getPassword());

        Thread.sleep(1200 + (long)(Math.random() * 1600));
        page.click(LOG_IN_BUTTON);

        playwrightHelper.waitForSelectorAndAct(15000, page, CAPTCHA, locator -> {
            throw new CaptchaException("Captcha appeared");
        });
    }

    public void processSignUp(Page page, TikTokAccount tikTokAccount) {
        LocalDate dotDate = LocalDate.parse(tikTokAccount.getDob().getDate().substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            log.info("Opening browser");
            page.navigate(TIKTOK_SIGN_UP_BROWSER_URL, new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));

            playwrightHelper.waitForSelectorAndAct(7000, page, BROWSER_CAPTCHA, locator -> {
                throw new BrowserCaptchaException("Captcha or unusual traffic detected");
            });

            playwrightHelper.waitForSelectorAndAct(20000, page, HOME_SIGN_UP, Locator::click);
            log.info("Starting account creation");

            playwrightHelper.waitForSelectorAndAct(20000, page, LANGUAGE_SELECT, locator -> {
                String selectedValue = locator.evaluate("el => el.value").toString();
                if (!selectedValue.equals("en")) {
                    playwrightHelper.waitForSelectorAndAct(15000, page, LANGUAGE_SELECT, Locator::click);
                    page.selectOption(LANGUAGE_SELECT, "en");
                }
            });

            waitRandomlyInRange(1200, 2200);
            page.click(SIGN_UP_USE_PHONE_OR_EMAIL);
            waitRandomlyInRange(1200, 2300);

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
            try {
                page.click(SEND_CODE_BUTTON);
            } catch (Exception e) {
                throw new SendCodeButtonNotWorkedException("Sending code to email took to long");
            }
            waitRandomlyInRange(1300, 1700);

            playwrightHelper.waitForSelectorAndAct(20000 ,page, CAPTCHA, locator -> {
                throw new CaptchaException("Captcha appeared");
            });

            page.waitForSelector(RESEND_CODE_TIMEOUT);

            waitRandomlyInRange(1200, 1700);
            String codeFromGeneratedEmail = mailTmService.getCodeFromGeneratedEmail(tikTokAccount.getEmail(), tikTokAccount.getPassword());

            waitRandomlyInRange(1300, 1900);
            page.fill(CODE_INPUT, codeFromGeneratedEmail);

            waitRandomlyInRange(1800, 2100);
            page.click(NEXT_BUTTON);

            Locator signUpLocator = page.locator(SIGN_UP_BUTTON);
            playwrightHelper.waitForSelector(signUpLocator, 60000);
            waitRandomlyInRange(1000, 1500);

            page.waitForSelector(USERNAME_INPUT);
            page.fill(USERNAME_INPUT, tikTokAccount.getUsername());
            waitRandomlyInRange(1000, 1700);

            page.waitForSelector(SIGN_UP_BUTTON);
            page.click(SIGN_UP_BUTTON);

            playwrightHelper.waitForSelectorAndAct(20000, page, SELECT_ADD, Locator::click);
            waitRandomlyInRange(1000, 1400);

        } catch (InterruptedException e) {
            log.error("InterruptedException: {}", e.getMessage());
            throw new ServerException("Something went wrong with account registering");
        }
    }

    public boolean isLoggedIn(Page page) {
        Locator avatarIcon = page.locator(AVATAR_ICON);
        return playwrightHelper.waitForSelector(avatarIcon, 30000);
    }
}
