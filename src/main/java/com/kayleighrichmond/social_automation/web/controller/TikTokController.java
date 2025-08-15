package com.kayleighrichmond.social_automation.web.controller;

import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.service.account.tiktok.TikTokCreatorFacade;
import com.kayleighrichmond.social_automation.service.account.tiktok.TikTokService;
import com.kayleighrichmond.social_automation.type.Status;
import com.kayleighrichmond.social_automation.web.controller.validator.TikTokAccountCreationValidator;
import com.kayleighrichmond.social_automation.web.dto.tiktok.CreateAccountRequest;
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

    @PostMapping("/all")
    public void createAccounts(@RequestBody @Valid CreateAccountRequest createAccountRequest) {
        tikTokAccountCreationValidator.validate(createAccountRequest);
        tikTokCreatorFacade.processAccountCreation(createAccountRequest);
    }

    @GetMapping
    public ResponseEntity<List<TikTokAccount>> findAll() {
        return ResponseEntity.ok(tikTokService.findAll());
    }

    @DeleteMapping("/all")
    public void deleteByStatus(@RequestParam Status status) {
        tikTokService.deleteAllByStatus(status);
    }
}