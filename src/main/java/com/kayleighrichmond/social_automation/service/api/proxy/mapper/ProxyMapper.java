package com.kayleighrichmond.social_automation.service.api.proxy.mapper;

import com.kayleighrichmond.social_automation.domain.entity.Proxy;
import com.kayleighrichmond.social_automation.web.controller.proxy.dto.AddProxyRequest;

public class ProxyMapper {

    public static Proxy mapProxyRequestToProxy(AddProxyRequest.ProxyRequest proxyRequest) {
        return Proxy.builder()
                .username(proxyRequest.getUsername())
                .password(proxyRequest.getPassword())
                .host(proxyRequest.getHost())
                .port(proxyRequest.getPort())
                .rebootLink(proxyRequest.getRebootLink())
                .build();
    }
}
