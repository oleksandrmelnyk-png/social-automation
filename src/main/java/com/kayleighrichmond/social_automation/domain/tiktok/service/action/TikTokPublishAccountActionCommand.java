package com.kayleighrichmond.social_automation.domain.tiktok.service.action;

import com.kayleighrichmond.social_automation.common.exception.CaptchaException;
import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.common.helper.WaitHelper;
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
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokConstants.TIKTOK_BASE_URL;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokSelectors.*;

@Slf4j
@Service
public class TikTokPublishAccountActionCommand extends TikTokAccountActionCommand {

    private final TikTokService tikTokService;

    private final PlaywrightHelper playwrightHelper;

    public TikTokPublishAccountActionCommand(TikTokService tikTokService, PlaywrightHelper playwrightHelper, PlaywrightInitializer playwrightInitializer, TikTokPlaywrightHelper tikTokPlaywrightHelper, TikTokActionExceptionHandler tikTokActionExceptionHandler) {
        super(tikTokService, playwrightHelper, playwrightInitializer, tikTokPlaywrightHelper, tikTokActionExceptionHandler);
        this.tikTokService = tikTokService;
        this.playwrightHelper = playwrightHelper;
    }

    @Override
    protected void tearDownAccountAction(TikTokAccount tikTokAccount, ProcessActionRequest processActionRequest) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.ACTED)
                .publishedPosts(tikTokAccount.getPublishedPosts() + 1)
                .executionMessage("")
                .build();
        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }

    @Override
    protected void startAction(PlaywrightDto playwrightDto, int actionsCount) throws InterruptedException {
        log.info("Starting publishing videos");

        Page page = playwrightDto.getPage();
        page.click(UPLOAD_DIV);
        page.waitForLoadState();

        try {
            ClassPathResource resource = new ClassPathResource("static/my_cat.mp4");
            Path tempFile = Files.createTempFile("upload-", ".mp4");
            Files.copy(resource.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            String videoPath = tempFile.toAbsolutePath().toString();
            page.setInputFiles(UPLOAD_VIDEO_INPUT, Path.of(videoPath));

        } catch (IOException e) {
            throw new ServerException("Video upload failed");
        }

        playwrightHelper.waitForSelectorAndAct(12000, page, TURN_OR_OPTIONS, Locator::click);
        WaitHelper.waitRandomlyInRange(1000, 2000);

        playwrightHelper.waitForSelectorAndAct(25000, page, UPLOADED_SPAN, locator -> {
            if (!locator.isVisible()) {
                throw new ServerException("Video upload took too long");
            }
        });
        log.info("Video uploaded successfully");

        page.click(POST_BUTTON);
        page.navigate(TIKTOK_BASE_URL, new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));

        playwrightHelper.waitForSelectorAndAct(page, VIDEO_PUBLISHED_SPAN, locator -> {
            page.navigate(TIKTOK_BASE_URL);
            page.waitForLoadState();
        });
    }

    @Override
    public Action getAction() {
        return Action.PUBLISH;
    }
}
