package com.kayleighrichmond.social_automation.web.controller.validator;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.service.proxy.ProxyMapper;
import com.kayleighrichmond.social_automation.service.proxy.ProxyVerifier;
import com.kayleighrichmond.social_automation.service.proxy.exception.ProxyNotVerifiedException;
import com.kayleighrichmond.social_automation.web.dto.proxy.AddProxyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AddProxiesValidator implements Validator {

    private final ProxyVerifier proxyVerifier;

    @Override
    public void validate(Object target) {
        boolean verified = verifyArgument(target.getClass());
        if (!verified) {
            throw new IllegalArgumentException("AddProxyRequest required");
        }

        AddProxyRequest addProxyRequest = (AddProxyRequest) target;
        List<AddProxyRequest.ProxyRequest> proxies = addProxyRequest.getProxies();

        for (AddProxyRequest.ProxyRequest proxy : proxies) {
            Proxy mappedProxy = ProxyMapper.mapProxyRequestToProxy(proxy);
            boolean verifiedProxy = proxyVerifier.verifyProxy(mappedProxy, false);

            if (!verifiedProxy) {
                throw new ProxyNotVerifiedException("Proxy %s not verified".formatted(proxy));
            }
        }
    }

    @Override
    public boolean verifyArgument(Class<?> clazz) {
        return clazz.equals(AddProxyRequest.class);
    }
}
