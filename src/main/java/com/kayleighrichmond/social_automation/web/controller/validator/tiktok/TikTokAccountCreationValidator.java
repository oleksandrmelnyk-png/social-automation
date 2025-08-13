package com.kayleighrichmond.social_automation.web.controller.validator.tiktok;

import com.kayleighrichmond.social_automation.service.account.tiktok.TikTokService;
import com.kayleighrichmond.social_automation.type.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TikTokAccountCreationValidator {

    private final TikTokService tikTokService;

    public void validate() {
        tikTokService.throwIfAccountsInProgressExists(Status.IN_PROGRESS);
    }

}
