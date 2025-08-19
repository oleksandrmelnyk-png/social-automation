package com.kayleighrichmond.social_automation.service.api.account.tiktok;

import com.kayleighrichmond.social_automation.domain.entity.account.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.type.Platform;
import com.kayleighrichmond.social_automation.exception.ServerException;
import com.kayleighrichmond.social_automation.service.api.account.LikeHandler;
import com.kayleighrichmond.social_automation.service.client.playwright.PlaywrightHelper;
import com.kayleighrichmond.social_automation.service.client.playwright.dto.PlaywrightDto;
import com.kayleighrichmond.social_automation.service.http.PlaywrightInitializer;
import com.kayleighrichmond.social_automation.web.controller.social.dto.LikePostsRequest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.kayleighrichmond.social_automation.service.api.account.WaitHelper.waitRandomlyInRange;
import static com.kayleighrichmond.social_automation.service.api.account.tiktok.TikTokConstants.TIKTOK_FOR_YOU_URL;
import static com.kayleighrichmond.social_automation.service.api.account.tiktok.TikTokConstants.TIKTOK_SIGN_IN_BROWSER_URL;
import static com.kayleighrichmond.social_automation.service.api.account.tiktok.TikTokSelectors.*;
import static com.kayleighrichmond.social_automation.service.api.account.tiktok.TikTokSelectors.LANGUAGE_SELECT;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokLikeHandler implements LikeHandler {

    private final TikTokService tikTokService;

    private final PlaywrightHelper playwrightHelper;

    private final PlaywrightInitializer playwrightInitializer;

    @Override
    public void processLikePosts(String accountId, LikePostsRequest likePostsRequest) {
        TikTokAccount tikTokAccount = tikTokService.findById(accountId);
        PlaywrightDto playwrightDto = playwrightInitializer.initPlaywright(tikTokAccount.getNstProfileId());
        Page page = playwrightDto.getPage();

        try {
            log.info("Opening browser");
            page.navigate(TIKTOK_FOR_YOU_URL);
            page.waitForLoadState();

            if (!isLoggedIn(page)) {
                log.info("User not signed in. processing logging");
                processLogIn(page, tikTokAccount);
            }

            processLiking(page, likePostsRequest.getLikes());
            log.info("Successfully finished liking videos");
        } catch (InterruptedException e) {
            // TODO add Error handler for throwing NstBrowserException in creation account as well
            log.error("InterruptedException: {}", e.getMessage());
            throw new ServerException("Something went wrong with posts liking");
        }
    }

    private boolean isLoggedIn(Page page) {
        Locator avatarIcon = page.locator(AVATAR_ICON);
        return playwrightHelper.waitForSelector(avatarIcon);
    }

    private void processLogIn(Page page, TikTokAccount tikTokAccount) throws InterruptedException {

        page.navigate(TIKTOK_SIGN_IN_BROWSER_URL);

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

        page.waitForLoadState();
    }

    private void processLiking(Page page, int likes) throws InterruptedException {
        log.info("Starting liking videos");
        int liked = 0;
        Random random = new Random();

        while (liked < likes) {
            boolean isLike = random.nextBoolean();
            if (isLike) {
                watchVideoAndLike(page);
                liked++;
                log.info("Liked video");
            } else {
                waitRandomlyInRange(1000, 3000);
            }

            page.click(NEXT_VIDEO_BUTTON);
            waitRandomlyInRange(2200, 3000);
        }
    }

    private void watchVideoAndLike(Page page) throws InterruptedException {
        // TODO more human-like actions get video's length and, for example don't watch till the end it it's too long or it has not enough likes

        waitRandomlyInRange(3000, 8000);
        page.click(LIKE_BUTTON);
    }


    @Override
    public Platform getPlatform() {
        return Platform.TIKTOK;
    }
}
