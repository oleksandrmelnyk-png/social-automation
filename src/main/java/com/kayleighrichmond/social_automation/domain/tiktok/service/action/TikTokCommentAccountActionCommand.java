package com.kayleighrichmond.social_automation.domain.tiktok.service.action;

import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.config.PlaywrightInitializer;
import com.kayleighrichmond.social_automation.domain.tiktok.common.exception.TikTokActionExceptionHandler;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.common.helper.TikTokPlaywrightHelper;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokService;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import com.kayleighrichmond.social_automation.system.client.playwright.PlaywrightHelper;
import com.kayleighrichmond.social_automation.system.client.playwright.dto.PlaywrightDto;
import com.kayleighrichmond.social_automation.system.controller.dto.ProcessActionRequest;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.kayleighrichmond.social_automation.common.helper.WaitHelper.waitRandomlyInRange;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokSelectors.*;

@Slf4j
@Service
public class TikTokCommentAccountActionCommand extends TikTokAccountActionCommand {

    private final TikTokService tikTokService;

    private final TikTokPlaywrightHelper tikTokPlaywrightHelper;

    public TikTokCommentAccountActionCommand(TikTokService tikTokService, PlaywrightHelper playwrightHelper, PlaywrightInitializer playwrightInitializer, TikTokPlaywrightHelper tikTokPlaywrightHelper, TikTokActionExceptionHandler tikTokActionExceptionHandler) {
        super(tikTokService, playwrightHelper, playwrightInitializer, tikTokPlaywrightHelper, tikTokActionExceptionHandler);
        this.tikTokService = tikTokService;
        this.tikTokPlaywrightHelper = tikTokPlaywrightHelper;
    }

    @Override
    protected void tearDownAccountAction(TikTokAccount tikTokAccount, ProcessActionRequest processActionRequest) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.ACTED)
                .commentedPosts(tikTokAccount.getCommentedPosts() + processActionRequest.getActionsCount())
                .executionMessage("")
                .build();
        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }

    @Override
    protected void startAction(PlaywrightDto playwrightDto, int actionsCount) throws InterruptedException {
        try {
            log.info("Starting commenting videos");

            Page page = playwrightDto.getPage();
            openCommentSection(page);

            Random random = new Random();

            int liked = 0, videoIndex = 0;
            while (liked < actionsCount) {
                boolean isDecidedToLike = random.nextBoolean();
                if (!tikTokPlaywrightHelper.isLive(page, videoIndex) && isDecidedToLike) {
                    watchVideoAndComment(page);
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
        return Action.COMMENT;
    }

    private void openCommentSection(Page page) throws InterruptedException {
        page.click(selectCommentButton(0));
        waitRandomlyInRange(1000, 3000);
    }

    private void watchVideoAndComment(Page page) throws InterruptedException {
        waitRandomlyInRange(2000, 5000);
        page.focus(COMMENT_TEXT_DIV);
        waitRandomlyInRange(1000, 3000);

        page.fill(COMMENT_TEXT_DIV, "\uD83D\uDE10");
        waitRandomlyInRange(1000, 3000);

        page.click(POST_COMMENT_DIV);
        waitRandomlyInRange(1000, 3000);
    }
}