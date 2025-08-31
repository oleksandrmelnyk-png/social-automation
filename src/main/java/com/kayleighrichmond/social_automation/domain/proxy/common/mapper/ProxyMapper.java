package com.kayleighrichmond.social_automation.domain.proxy.common.mapper;

import com.kayleighrichmond.social_automation.common.mapper.SimpleMapper;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.AddProxyRequest;
import org.springframework.stereotype.Component;

@Component
public class ProxyMapper implements SimpleMapper<AddProxyRequest.ProxyRequest, Proxy> {

    @Override
    public Proxy mapDtoToEntity(AddProxyRequest.ProxyRequest dto) {
        return Proxy.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .host(dto.getHost())
                .port(dto.getPort())
                .rebootLink(dto.getRebootLink())
                .autoRotateInterval(dto.getAutoRotateInterval())
                .build();
    }

}
