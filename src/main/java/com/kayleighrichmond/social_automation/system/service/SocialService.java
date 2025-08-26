package com.kayleighrichmond.social_automation.system.service;

import com.kayleighrichmond.social_automation.common.command.AccountCommand;
import com.kayleighrichmond.social_automation.common.command.ActionCommand;
import com.kayleighrichmond.social_automation.common.command.ProxyCommand;
import com.kayleighrichmond.social_automation.common.factory.*;
import com.kayleighrichmond.social_automation.system.controller.dto.ActionRequest;
import com.kayleighrichmond.social_automation.system.controller.dto.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final AccountFactory accountFactory;

    private final ProxyFactory proxyFactory;

    private final ActionFactory actionFactory;

    public void processAccountsCreation(CreateAccountsRequest createAccountsRequest) {
        AccountCommand accountCommand = accountFactory.getAccountCommand(createAccountsRequest.getPlatform());
        accountCommand.executeAccountCreation(createAccountsRequest);
    }

    public void processAction(String accountId, ActionRequest actionRequest) {
        ProxyCommand proxyCommand = proxyFactory.getProxyCommand(actionRequest.getPlatform());
        proxyCommand.executeActiveProxy(accountId);

        ActionCommand actionCommand = actionFactory.getActionCommand(actionRequest.getPlatform(), actionRequest.getAction());
        actionCommand.executeAction(accountId, actionRequest);
    };
}
