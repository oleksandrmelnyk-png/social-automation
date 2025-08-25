package com.kayleighrichmond.social_automation.system.service;

import com.kayleighrichmond.social_automation.common.*;
import com.kayleighrichmond.social_automation.common.registry.*;
import com.kayleighrichmond.social_automation.system.controller.dto.ActionRequest;
import com.kayleighrichmond.social_automation.system.controller.dto.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final AccountRegistry accountRegistry;

    private final ProxyResolverRegistry proxyResolverRegistry;

    private final ActionHandlerRegistry actionHandlerRegistry;

    public void processAccountsCreation(CreateAccountsRequest createAccountsRequest) {
        AccountCreator accountCreator = accountRegistry.getAccountCreator(createAccountsRequest.getPlatform());
        accountCreator.processAccountCreation(createAccountsRequest);
    }

    public void processAction(String accountId, ActionRequest actionRequest) {
        ProxyResolver proxyResolver = proxyResolverRegistry.getProxyResolver(actionRequest.getPlatform());
        proxyResolver.resolveActiveProxy(accountId);

        ActionHandler actionHandler = actionHandlerRegistry.getActionHandler(actionRequest.getPlatform(), actionRequest.getAction());
        actionHandler.processAction(accountId, actionRequest);
    };
}
