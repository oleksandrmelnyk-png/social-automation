package com.kayleighrichmond.social_automation.domain.tiktok.service;

import com.kayleighrichmond.social_automation.common.exception.AccountIsInActionException;
import com.kayleighrichmond.social_automation.common.exception.AccountsCurrentlyCreatingException;
import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.type.Status;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.common.LikeActionHandler;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import com.kayleighrichmond.social_automation.system.client.playwright.PlaywrightHelper;
import com.kayleighrichmond.social_automation.system.client.playwright.dto.PlaywrightDto;
import com.kayleighrichmond.social_automation.config.PlaywrightInitializer;
import com.kayleighrichmond.social_automation.system.controller.dto.LikePostsRequest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.kayleighrichmond.social_automation.common.helper.WaitHelper.waitRandomlyInRange;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokConstants.TIKTOK_FOR_YOU_URL;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokConstants.TIKTOK_SIGN_IN_BROWSER_URL;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokSelectors.*;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokSelectors.LANGUAGE_SELECT;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokLikeActionHandler implements LikeActionHandler {

    private final TikTokService tikTokService;

    private final PlaywrightHelper playwrightHelper;

    private final PlaywrightInitializer playwrightInitializer;

    @Override
    public void processLikePosts(String accountId, LikePostsRequest likePostsRequest) {
        TikTokAccount tikTokAccount = getAccountIfNotInActionAndNotInProgress(accountId);
        try {
            tikTokService.update(tikTokAccount.getId(), UpdateAccountRequest.builder().action(Action.LIKING).build());
            initializeNstAndStartAccountCreation(tikTokAccount, likePostsRequest);

            UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                    .action(Action.ACTED)
                    .likedPosts(tikTokAccount.getLikedPosts() + likePostsRequest.getLikesCount())
                    .build();
            tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
        } catch (Error | Exception e) {
            // TODO add an Error handler for throwing NstBrowserException in creation account as well
            tikTokService.updateFromActionStatusInProgressToFailedById(accountId, "Unexpected server exception");
            log.error(e.getMessage());
            throw new ServerException("Something went wrong with posts liking");
        }
    }

    private void initializeNstAndStartAccountCreation(TikTokAccount tikTokAccount, LikePostsRequest likePostsRequest) throws InterruptedException {
        PlaywrightDto playwrightDto = playwrightInitializer.initPlaywright(tikTokAccount.getNstProfileId());
        Page page = playwrightDto.getPage();

        log.info("Opening browser");
        page.navigate(TIKTOK_FOR_YOU_URL);
        page.waitForLoadState();

        if (!isLoggedIn(page)) {
            log.info("User not signed in. processing logging");
            processLogIn(page, tikTokAccount);
        }

        playwrightHelper.waitForSelectorAndAct(page, SELECT_ADD, Locator::click);
        waitRandomlyInRange(1000, 1400);

        processLiking(playwrightDto, likePostsRequest.getLikesCount());
        log.info("Successfully finished liking videos");
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

    private void processLiking(PlaywrightDto playwrightDto, int likes) throws InterruptedException {
        try {
            log.info("Starting liking videos");

            Page page = playwrightDto.getPage();
            Random random = new Random();

            int liked = 0, videoIndex = 0;
            while (liked < likes) {
                boolean isDecidedToLike = random.nextBoolean();
                if (isDecidedToLike && !isLive(page, videoIndex)) {
                    watchVideoAndLike(page, videoIndex);
                    liked++;
                }

                waitRandomlyInRange(1000, 3000);
                page.click(NEXT_VIDEO_BUTTON);
                videoIndex++;
            }
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

    private TikTokAccount getAccountIfNotInActionAndNotInProgress(String accountId) {
        TikTokAccount tikTokAccount = tikTokService.findById(accountId);

        if (tikTokAccount.getAction() != null
                && tikTokAccount.getAction() != Action.ACTED
                && tikTokAccount.getAction() != Action.FAILED) {
            throw new AccountIsInActionException("This account is already in action");
        }

        if (tikTokAccount.getStatus() == Status.IN_PROGRESS || tikTokAccount.getStatus() == Status.FAILED) {
            throw new AccountsCurrentlyCreatingException("This accounts is currently creating");
        }

        return tikTokAccount;
    }

    private void watchVideoAndLike(Page page, int videoIndex) throws InterruptedException {
        waitRandomlyInRange(2000, 5000);
        page.click(selectLikeButton(videoIndex));
    }

    private boolean isLoggedIn(Page page) {
        Locator avatarIcon = page.locator(AVATAR_ICON);
        return playwrightHelper.waitForSelector(avatarIcon, 10000);
    }

    private boolean isLive(Page page, int videoIndex) {
        Locator avatarIcon = page.locator(selectLiveNow(videoIndex));
        return playwrightHelper.waitForSelector(avatarIcon, 1000);
    }

    @Override
    public Platform getPlatform() {
        return Platform.TIKTOK;
    }
}
