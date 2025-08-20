package com.kayleighrichmond.social_automation.domain.proxy.web.controller;

import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.proxy.service.ProxyService;
import com.kayleighrichmond.social_automation.domain.proxy.web.validator.AddProxiesValidator;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.AddProxyRequest;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.UpdateProxyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/proxy")
public class ProxyController {

    private final ProxyService proxyService;

    private final AddProxiesValidator addProxiesValidator;

    @PostMapping("/all")
    public void addProxies(@Valid @RequestBody AddProxyRequest addProxyRequest) {
        addProxiesValidator.validate(addProxyRequest);
        proxyService.saveAll(addProxyRequest);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Proxy>> findAll() {
        return ResponseEntity.ok(proxyService.findAll());
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody UpdateProxyRequest updateProxyRequest) {
        proxyService.update(id, updateProxyRequest);
    }

    // TODO create endpoint for reverifying all proxies to avoid verification while accounts creation
}
