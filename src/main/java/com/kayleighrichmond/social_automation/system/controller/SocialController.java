package com.kayleighrichmond.social_automation.system.controller;

import com.kayleighrichmond.social_automation.system.controller.validator.SocialAccountLikeValidator;
import com.kayleighrichmond.social_automation.system.service.SocialService;
import com.kayleighrichmond.social_automation.system.controller.dto.LikePostsRequest;
import com.kayleighrichmond.social_automation.system.controller.validator.SocialAccountCreationValidator;
import com.kayleighrichmond.social_automation.system.controller.dto.CreateAccountsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
public class SocialController {

    private final SocialService socialService;

    private final SocialAccountCreationValidator socialAccountCreationValidator;

    private final SocialAccountLikeValidator socialAccountLikeValidator;

    @PostMapping("/create-accounts")
    public void createAccounts(@RequestBody @Valid CreateAccountsRequest createAccountsRequest) {
        socialAccountCreationValidator.validate(createAccountsRequest);
        socialService.processAccountsCreation(createAccountsRequest);
    }

    @PostMapping("/like-posts/account/{id}")
    public void likeAccounts(@PathVariable String id, @RequestBody LikePostsRequest likePostsRequest) {
        socialAccountLikeValidator.validate(id);
        socialService.processLikePosts(id, likePostsRequest);
    }
}
