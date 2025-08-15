package com.kayleighrichmond.social_automation.service.proxy;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.web.dto.proxy.AddProxyRequest;

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
