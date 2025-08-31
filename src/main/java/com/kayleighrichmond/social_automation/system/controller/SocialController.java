package com.kayleighrichmond.social_automation.system.controller;

import com.kayleighrichmond.social_automation.system.controller.dto.ProcessActionRequest;
import com.kayleighrichmond.social_automation.system.service.SocialService;
import com.kayleighrichmond.social_automation.system.controller.dto.CreateAccountsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
public class SocialController {

    private final SocialService socialService;

    @PostMapping("/create-accounts")
    public void createAccounts(@RequestBody @Valid CreateAccountsRequest createAccountsRequest) {
        socialService.processAccountsCreation(createAccountsRequest);
    }

    @PostMapping("/action")
    public void processAction(@RequestParam String accountId, @Valid @RequestBody ProcessActionRequest processActionRequest) {
        socialService.processAction(accountId, processActionRequest);
    }
}
