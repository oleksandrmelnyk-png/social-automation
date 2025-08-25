package com.kayleighrichmond.social_automation.domain.tiktok.service.action;

import com.kayleighrichmond.social_automation.common.ActionHandler;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokPlaywrightHelper;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokService;
import com.kayleighrichmond.social_automation.system.controller.dto.ActionRequest;
import com.kayleighrichmond.social_automation.common.exception.AccountIsInActionException;
import com.kayleighrichmond.social_automation.common.exception.AccountsCurrentlyCreatingException;
import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.common.type.Status;
import com.kayleighrichmond.social_automation.config.PlaywrightInitializer;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import com.kayleighrichmond.social_automation.system.client.playwright.PlaywrightHelper;
import com.kayleighrichmond.social_automation.system.client.playwright.dto.PlaywrightDto;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.kayleighrichmond.social_automation.common.helper.WaitHelper.waitRandomlyInRange;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokConstants.TIKTOK_FOR_YOU_URL;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokSelectors.SELECT_ADD;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class TikTokActionHandler implements ActionHandler {

    private final TikTokService tikTokService;

    private final PlaywrightHelper playwrightHelper;

    private final PlaywrightInitializer playwrightInitializer;

    private final TikTokPlaywrightHelper tikTokPlaywrightHelper;

    @Override
    public void processAction(String accountId, ActionRequest actionRequest) {
        TikTokAccount tikTokAccount = getAccountIfNotInActionAndNotInProgress(accountId);
        try {
            tikTokService.update(tikTokAccount.getId(), UpdateAccountRequest.builder().action(actionRequest.getAction()).build());
            initializeNstAndStartAccountCreation(tikTokAccount, actionRequest);
            tearDownAccountAction(tikTokAccount, actionRequest);
        } catch (Error | Exception e) {
            // TODO add an Error handler for throwing NstBrowserException in creation account as well
            tikTokService.updateFromActionStatusInProgressToFailedById(accountId, "Unexpected server exception");
            log.error(e.getMessage());
            throw new ServerException("Something went wrong with posts liking");
        }
    }

    private void initializeNstAndStartAccountCreation(TikTokAccount tikTokAccount, ActionRequest actionRequest) throws InterruptedException {
        PlaywrightDto playwrightDto = playwrightInitializer.initPlaywright(tikTokAccount.getNstProfileId());
        Page page = playwrightDto.getPage();

        log.info("Opening browser");
        page.navigate(TIKTOK_FOR_YOU_URL);
        page.waitForLoadState();

        if (!tikTokPlaywrightHelper.isLoggedIn(page)) {
            log.info("User not signed in. processing logging");
            tikTokPlaywrightHelper.processLogIn(page, tikTokAccount);
        }

        playwrightHelper.waitForSelectorAndAct(page, SELECT_ADD, Locator::click);
        waitRandomlyInRange(1000, 1400);

        startAction(playwrightDto, actionRequest.getActionsCount());
        log.info("Successfully finished liking videos");
    }

    private TikTokAccount getAccountIfNotInActionAndNotInProgress(String accountId) {
        TikTokAccount tikTokAccount = tikTokService.findById(accountId);

        if (tikTokAccount.getAction() == Action.COMMENT
                || tikTokAccount.getAction() == Action.LIKE
                || tikTokAccount.getAction() == Action.PUBLISH
        ) {
            throw new AccountIsInActionException("This account is already in action");
        }

        if (tikTokAccount.getStatus() == Status.IN_PROGRESS
                || tikTokAccount.getStatus() == Status.FAILED
        ) {
            throw new AccountsCurrentlyCreatingException("This account is currently creating");
        }

        return tikTokAccount;
    }

    protected abstract void tearDownAccountAction(TikTokAccount tikTokAccount, ActionRequest actionRequest);

    protected abstract void startAction(PlaywrightDto playwrightDto, int actionsCount) throws InterruptedException;

    @Override
    public abstract Action getAction();

    @Override
    public Platform getPlatform() {
        return Platform.TIKTOK;
    }
}
