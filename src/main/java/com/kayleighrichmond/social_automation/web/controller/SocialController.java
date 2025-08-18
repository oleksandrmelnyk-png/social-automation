package com.kayleighrichmond.social_automation.web.controller;

import com.kayleighrichmond.social_automation.service.api.social.SocialService;
import com.kayleighrichmond.social_automation.web.controller.validator.SocialAccountCreationValidator;
import com.kayleighrichmond.social_automation.web.dto.tiktok.CreateAccountsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
public class SocialController {

    private final SocialService socialService;

    private final SocialAccountCreationValidator socialAccountCreationValidator;

    @PostMapping
    public void createAccounts(@RequestBody @Valid CreateAccountsRequest createAccountsRequest) {
        socialAccountCreationValidator.validate(createAccountsRequest);
        socialService.processAccountsCreation(createAccountsRequest);
    }
}
