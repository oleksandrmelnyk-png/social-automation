package com.kayleighrichmond.social_automation.service.api.social;

import com.kayleighrichmond.social_automation.service.api.account.AccountCreator;
import com.kayleighrichmond.social_automation.service.api.account.LikeHandler;
import com.kayleighrichmond.social_automation.service.api.account.registry.AccountRegistry;
import com.kayleighrichmond.social_automation.service.api.account.registry.LikeHandlerRegistry;
import com.kayleighrichmond.social_automation.web.controller.social.dto.LikePostsRequest;
import com.kayleighrichmond.social_automation.web.controller.social.dto.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SocialService {

    private final AccountRegistry accountRegistry;

    private final LikeHandlerRegistry likeHandlerRegistry;

    public void processAccountsCreation(CreateAccountsRequest createAccountsRequest) {
        AccountCreator accountCreator = accountRegistry.getAccountCreator(createAccountsRequest.getPlatform());
        accountCreator.processAccountCreation(createAccountsRequest);
    }

    public void processLikePosts(String accountId, LikePostsRequest likePostsRequest) {
        LikeHandler likeHandler = likeHandlerRegistry.getLikeHandler(likePostsRequest.getPlatform());
        likeHandler.processLikePosts(accountId, likePostsRequest);
    }
}
