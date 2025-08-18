package com.kayleighrichmond.social_automation.web.controller.validator;

import com.kayleighrichmond.social_automation.domain.entity.Proxy;
import com.kayleighrichmond.social_automation.service.api.proxy.mapper.ProxyMapper;
import com.kayleighrichmond.social_automation.service.api.proxy.ProxyVerifier;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.ProxyNotVerifiedException;
import com.kayleighrichmond.social_automation.web.dto.proxy.AddProxyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddProxiesValidator implements Validator {

    private final ProxyVerifier proxyVerifier;

    @Override
    public void validate(Object target) {
        verifyArgument(target.getClass());
        AddProxyRequest addProxyRequest = (AddProxyRequest) target;

        List<AddProxyRequest.ProxyRequest> proxies = addProxyRequest.getProxies();

        for (AddProxyRequest.ProxyRequest proxy : proxies) {
            Proxy mappedProxy = ProxyMapper.mapProxyRequestToProxy(proxy);
            boolean verifiedProxy = proxyVerifier.verifyProxy(mappedProxy, false);

            if (!verifiedProxy) {
                log.warn("Proxy {} not verified", proxy);
                throw new ProxyNotVerifiedException("Proxy %s has not verified".formatted(proxy.getUsername()));
            }
        }
    }

    @Override
    public void verifyArgument(Class<?> clazz) throws IllegalArgumentException {
        boolean verified = clazz.equals(AddProxyRequest.class);
        if (!verified) {
            throw new IllegalArgumentException("AddProxyRequest required");
        }
    }
}
