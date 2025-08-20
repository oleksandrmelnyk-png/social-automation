package com.kayleighrichmond.social_automation.domain.tiktok.web.controller;

import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokService;
import com.kayleighrichmond.social_automation.common.type.Status;
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