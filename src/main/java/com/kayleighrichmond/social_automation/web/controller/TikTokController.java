package com.kayleighrichmond.social_automation.web.controller;

import com.kayleighrichmond.social_automation.domain.entity.account.TikTokAccount;
import com.kayleighrichmond.social_automation.service.api.account.tiktok.TikTokService;
import com.kayleighrichmond.social_automation.domain.type.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tiktok")
@RequiredArgsConstructor
public class TikTokController {

    private final TikTokService tikTokService;

    @GetMapping("/all")
    public ResponseEntity<List<TikTokAccount>> findAll() {
        return ResponseEntity.ok(tikTokService.findAll());
    }

    @DeleteMapping("/all")
    public void deleteByStatus(@RequestParam Status status) {
        tikTokService.deleteAllByStatus(status);
    }
}