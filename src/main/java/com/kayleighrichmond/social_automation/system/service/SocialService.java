package com.kayleighrichmond.social_automation.system.service;

import com.kayleighrichmond.social_automation.common.AccountCreator;
import com.kayleighrichmond.social_automation.common.LikeActionHandler;
import com.kayleighrichmond.social_automation.common.ProxyResolver;
import com.kayleighrichmond.social_automation.common.registry.AccountRegistry;
import com.kayleighrichmond.social_automation.common.registry.LikeHandlerRegistry;
import com.kayleighrichmond.social_automation.common.registry.ProxyResolverRegistry;
import com.kayleighrichmond.social_automation.system.controller.dto.LikePostsRequest;
import com.kayleighrichmond.social_automation.system.controller.dto.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final AccountRegistry accountRegistry;

    private final LikeHandlerRegistry likeHandlerRegistry;

    private final ProxyResolverRegistry proxyResolverRegistry;

    public void processAccountsCreation(CreateAccountsRequest createAccountsRequest) {
        AccountCreator accountCreator = accountRegistry.getAccountCreator(createAccountsRequest.getPlatform());
        accountCreator.processAccountCreation(createAccountsRequest);
    }

    public void processLikePosts(String accountId, LikePostsRequest likePostsRequest) {
        ProxyResolver proxyResolver = proxyResolverRegistry.getProxyResolver(likePostsRequest.getPlatform());
        proxyResolver.resolveActiveProxy(accountId);

        LikeActionHandler likeActionHandler = likeHandlerRegistry.getLikeHandler(likePostsRequest.getPlatform());
        likeActionHandler.processLikePosts(accountId, likePostsRequest);
    }
}
