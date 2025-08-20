package com.kayleighrichmond.social_automation.domain.proxy.web.validator;

import com.kayleighrichmond.social_automation.common.validator.Validator;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.proxy.common.mapper.ProxyMapper;
import com.kayleighrichmond.social_automation.domain.proxy.service.ProxyVerifier;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.ProxyNotVerifiedException;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.AddProxyRequest;
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
