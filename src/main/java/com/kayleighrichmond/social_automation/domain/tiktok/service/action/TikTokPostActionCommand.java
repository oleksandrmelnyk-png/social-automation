package com.kayleighrichmond.social_automation.domain.tiktok.service.action;

import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.common.helper.WaitHelper;
import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.config.PlaywrightInitializer;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.common.helper.TikTokPlaywrightHelper;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokService;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import com.kayleighrichmond.social_automation.system.client.playwright.PlaywrightHelper;
import com.kayleighrichmond.social_automation.system.client.playwright.dto.PlaywrightDto;
import com.kayleighrichmond.social_automation.system.controller.dto.ActionRequest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokConstants.TIKTOK_FOR_YOU_URL;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokSelectors.*;

@Slf4j
@Service
public class TikTokPostActionCommand extends TikTokActionCommand {

    private final TikTokService tikTokService;

    private final PlaywrightHelper playwrightHelper;

    public TikTokPostActionCommand(TikTokService tikTokService, PlaywrightHelper playwrightHelper, PlaywrightInitializer playwrightInitializer, TikTokPlaywrightHelper tikTokPlaywrightHelper) {
        super(tikTokService, playwrightHelper, playwrightInitializer, tikTokPlaywrightHelper);
        this.tikTokService = tikTokService;
        this.playwrightHelper = playwrightHelper;
    }

    @Override
    protected void tearDownAccountAction(TikTokAccount tikTokAccount, ActionRequest actionRequest) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.ACTED)
                .publishedPosts(tikTokAccount.getPublishedPosts() + actionRequest.getActionsCount())
                .executionMessage(null)
                .build();
        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }

    @Override
    protected void startAction(PlaywrightDto playwrightDto, int actionsCount) throws InterruptedException {
        log.info("Starting publishing videos");

        Page page = playwrightDto.getPage();
        page.click(UPLOAD_DIV);
        page.waitForLoadState();

        String videoPath = new File("src/main/resources/static/my_cat.mp4").getAbsolutePath();
        page.setInputFiles(UPLOAD_VIDEO_INPUT, Path.of(videoPath));

        Locator cancelOptionsLocator = page.locator(CANCEL_BUTTON);
        boolean optionsAppeared = playwrightHelper.waitForSelector(cancelOptionsLocator, 1000);
        if (optionsAppeared) {
            page.click(CANCEL_BUTTON);
            WaitHelper.waitRandomlyInRange(1000, 2000);
        }

        Locator uploadedLocator = page.locator(UPLOADED_SPAN);
        boolean uploadedAppeared = playwrightHelper.waitForSelector(uploadedLocator, 20000);
        if (!uploadedAppeared) {
           throw new ServerException("Video upload took too long");
        }
        log.info("Video uploaded successfully");

        page.click(POST_BUTTON);
        page.waitForLoadState();

        Locator publishedLocator = page.locator(VIDEO_PUBLISHED_SPAN);
        boolean publishedAppeared = playwrightHelper.waitForSelector(publishedLocator);
        if (publishedAppeared) {
            page.navigate(TIKTOK_FOR_YOU_URL);
            page.waitForLoadState();
        }
    }

    @Override
    public Action getAction() {
        return Action.PUBLISH;
    }
}
