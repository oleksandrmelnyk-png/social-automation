package com.kayleighrichmond.social_automation.web.controller;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.service.proxy.ProxyService;
import com.kayleighrichmond.social_automation.web.controller.validator.AddProxiesValidator;
import com.kayleighrichmond.social_automation.web.dto.proxy.AddProxyRequest;
import com.kayleighrichmond.social_automation.web.dto.proxy.UpdateProxyRequest;
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

    @PostMapping("/add")
    public void addProxies(@Valid @RequestBody AddProxyRequest addProxyRequest) {
        addProxiesValidator.validate(addProxyRequest);
        proxyService.saveAll(addProxyRequest);
    }

    @GetMapping
    public ResponseEntity<List<Proxy>> findAll() {
        return ResponseEntity.ok(proxyService.findAll());
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody UpdateProxyRequest updateProxyRequest) {
        proxyService.update(id, updateProxyRequest);
    }
}
