package com.kayleighrichmond.social_automation.web.controller;

import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.service.account.tiktok.TikTokCreatorFacade;
import com.kayleighrichmond.social_automation.service.account.tiktok.TikTokService;
import com.kayleighrichmond.social_automation.web.controller.validator.TikTokAccountCreationValidator;
import com.kayleighrichmond.social_automation.web.dto.account.CreateAccountsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tiktok")
@RequiredArgsConstructor
public class TikTokController {

    private final TikTokService tikTokService;

    private final TikTokAccountCreationValidator tikTokAccountCreationValidator;

    private final TikTokCreatorFacade tikTokCreatorFacade;

    @PostMapping
    public void createAccounts(@RequestBody @Valid CreateAccountsRequest createAccountsRequest) {
        tikTokAccountCreationValidator.validate(createAccountsRequest);
        tikTokCreatorFacade.processAccountCreation(createAccountsRequest);
    }

    @GetMapping
    public ResponseEntity<List<TikTokAccount>> findAll() {
        return ResponseEntity.ok(tikTokService.findAll());
    }
}
