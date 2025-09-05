package com.kayleighrichmond.social_automation.domain.tiktok.service.action;

import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.domain.tiktok.common.exception.TikTokActionExceptionHandler;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.common.helper.TikTokPlaywrightHelper;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokService;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import com.kayleighrichmond.social_automation.system.client.playwright.PlaywrightHelper;
import com.kayleighrichmond.social_automation.system.client.playwright.dto.PlaywrightDto;
import com.kayleighrichmond.social_automation.config.PlaywrightInitializer;
import com.kayleighrichmond.social_automation.system.controller.dto.ProcessActionRequest;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.kayleighrichmond.social_automation.common.helper.WaitHelper.waitRandomlyInRange;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokSelectors.*;

@Slf4j
@Service
public class TikTokLikeAccountActionCommand extends TikTokAccountActionCommand {

    private final TikTokPlaywrightHelper tikTokPlaywrightHelper;

    private final TikTokService tikTokService;

    public TikTokLikeAccountActionCommand(TikTokService tikTokService, PlaywrightHelper playwrightHelper, TikTokPlaywrightHelper tikTokPlaywrightHelper, PlaywrightInitializer playwrightInitializer, TikTokActionExceptionHandler tikTokActionExceptionHandler) {
        super(tikTokService, playwrightHelper, playwrightInitializer, tikTokPlaywrightHelper, tikTokActionExceptionHandler);
        this.tikTokPlaywrightHelper = tikTokPlaywrightHelper;
        this.tikTokService = tikTokService;
    }

    @Override
    protected void tearDownAccountAction(TikTokAccount tikTokAccount, ProcessActionRequest processActionRequest) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.ACTED)
                .likedPosts(tikTokAccount.getLikedPosts() + processActionRequest.getActionsCount())
                .executionMessage("")
                .build();
        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }

    @Override
    protected void startAction(PlaywrightDto playwrightDto, int actionsCount) throws InterruptedException {
        try {
            log.info("Starting liking videos");

            Page page = playwrightDto.getPage();
            Random random = new Random();

            int liked = 0, videoIndex = 0;
            while (liked < actionsCount) {
                boolean isDecidedToLike = random.nextBoolean();
                if (isDecidedToLike && !tikTokPlaywrightHelper.isLive(page, videoIndex)) {
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

    @Override
    public Action getAction() {
        return Action.LIKE;
    }

    private void watchVideoAndLike(Page page, int videoIndex) throws InterruptedException {
        waitRandomlyInRange(2000, 5000);
        page.click(selectLikeButton(videoIndex));
    }
}
