package com.kayleighrichmond.social_automation.system.service;

import com.kayleighrichmond.social_automation.common.command.AccountsCreationCommand;
import com.kayleighrichmond.social_automation.common.command.AccountActionCommand;
import com.kayleighrichmond.social_automation.common.command.AccountActionProxyCommand;
import com.kayleighrichmond.social_automation.common.command.AccountsCreationProxyCommand;
import com.kayleighrichmond.social_automation.common.factory.*;
import com.kayleighrichmond.social_automation.system.controller.dto.ProcessActionRequest;
import com.kayleighrichmond.social_automation.system.controller.dto.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final AccountsCreationFactory accountsCreationFactory;

    private final ProxyFactory proxyFactory;

    private final ActionActionFactory actionActionFactory;

    private final AccountsCreationProxyFactory accountsCreationProxyFactory;

    public void processAccountsCreation(CreateAccountsRequest createAccountsRequest) {
        AccountsCreationProxyCommand accountCreationProxyCommand = accountsCreationProxyFactory.getAccountCreationProxyCommand(createAccountsRequest.getPlatform());
        accountCreationProxyCommand.executeAvailableProxies(createAccountsRequest);

        AccountsCreationCommand accountsCreationCommand = accountsCreationFactory.getAccountCommand(createAccountsRequest.getPlatform());
        accountsCreationCommand.executeAccountCreation(createAccountsRequest);
    }

    public void processAction(String accountId, ProcessActionRequest processActionRequest) {
        AccountActionProxyCommand accountActionProxyCommand = proxyFactory.getProxyCommand(processActionRequest.getPlatform());
        accountActionProxyCommand.executeActiveProxy(accountId);

        AccountActionCommand accountActionCommand = actionActionFactory.getActionCommand(processActionRequest.getPlatform(), processActionRequest.getAction());
        accountActionCommand.executeAction(accountId, processActionRequest);
    };
}
