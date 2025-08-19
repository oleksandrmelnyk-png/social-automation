package com.kayleighrichmond.social_automation.service.api.social;

import com.kayleighrichmond.social_automation.service.api.account.AccountCreator;
import com.kayleighrichmond.social_automation.service.api.account.LikeHandler;
import com.kayleighrichmond.social_automation.web.controller.social.dto.LikePostsRequest;
import com.kayleighrichmond.social_automation.web.controller.social.dto.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final SocialRegistry socialRegistry;

    public void processAccountsCreation(CreateAccountsRequest createAccountsRequest) {
        AccountCreator accountCreator = socialRegistry.getAccountCreator(createAccountsRequest.getPlatform());
        accountCreator.processAccountCreation(createAccountsRequest);
    }

    public void processLikePosts(String accountId, LikePostsRequest likePostsRequest) {
        LikeHandler likeHandler = socialRegistry.getLikeHandler(likePostsRequest.getPlatform());
        likeHandler.processLikePosts(accountId, likePostsRequest);
    }
}
