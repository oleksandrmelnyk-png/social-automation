package com.kayleighrichmond.social_automation.web.controller;

import com.kayleighrichmond.social_automation.service.proxy.ProxyService;
import com.kayleighrichmond.social_automation.web.dto.proxy.AddProxyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/proxy")
public class ProxyController {

    private final ProxyService proxyService;

    @PostMapping("/add-all")
    public void addProxies(@Valid @RequestBody AddProxyRequest addProxyRequest) {
        proxyService.saveAll(addProxyRequest);
    }
}
