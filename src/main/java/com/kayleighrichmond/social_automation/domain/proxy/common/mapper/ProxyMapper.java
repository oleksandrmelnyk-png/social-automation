package com.kayleighrichmond.social_automation.domain.proxy.common.mapper;

import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.AddProxyRequest;

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
