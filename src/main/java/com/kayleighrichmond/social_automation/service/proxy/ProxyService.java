package com.kayleighrichmond.social_automation.service.proxy;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.repository.ProxyRepository;
import com.kayleighrichmond.social_automation.service.ip_api.IpApiClient;
import com.kayleighrichmond.social_automation.service.ip_api.dto.GetProxyAddressResponse;
import com.kayleighrichmond.social_automation.service.proxy.exception.NoProxiesAvailableException;
import com.kayleighrichmond.social_automation.service.proxy.exception.ProxyAlreadyExistsException;
import com.kayleighrichmond.social_automation.service.proxy.exception.ProxyNotVerifiedException;
import com.kayleighrichmond.social_automation.web.dto.proxy.AddProxyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProxyService {

    private final ProxyVerifier proxyVerifier;

    private final ProxyRepository proxyRepository;

    private final IpApiClient ipApiClient;

    public List<Proxy> findAllVerifiedByCountryCodeAndAccountsLinked(String countryCode, int accountsLinkedMax, PageRequest pageRequest) {
        List<Proxy> proxies = proxyRepository.findAllByCountryCodeAndVerifiedAndAccountsLinkedLessThan(countryCode, true, accountsLinkedMax, pageRequest);

        if (proxies.isEmpty()) {
            throw new NoProxiesAvailableException("No proxies available by country code: " + countryCode);
        }

        return proxies;
    }

    public void saveAll(AddProxyRequest addProxyRequest) {
        List<Proxy> proxies = new ArrayList<>();
        List<AddProxyRequest.ProxyRequest> proxyRequests = addProxyRequest.getProxies();

        for (AddProxyRequest.ProxyRequest proxyRequest : proxyRequests) {
            throwIfProxyExistsByUsername(proxyRequest.getUsername());

            Proxy proxy = mapProxyRequestToProxy(proxyRequest);
            boolean verifiedProxy = proxyVerifier.verifyProxy(proxy, false);

            if (!verifiedProxy) {
                throw new ProxyNotVerifiedException("Proxy %s not verified".formatted(proxy));
            }

            GetProxyAddressResponse proxyAddress = ipApiClient.getProxyAddress(proxy);
            proxy.setCountryCode(proxyAddress.getCountryCode());

            proxies.add(proxy);
        }

        proxyRepository.saveAll(proxies);
    }

    public List<Proxy> verifyAll() {
        List<Proxy> proxies = proxyRepository.findAll();
        for (Proxy proxy : proxies) {
            boolean verifiedProxy = proxyVerifier.verifyProxy(proxy, false);
            if (!verifiedProxy) {
                proxy.setVerified(false);
                proxyRepository.save(proxy);
            }
        }

        return proxies;
    }

    public void updateAccountsLinked(String id, int accountsLinked) {
        Proxy proxy = findByIdOrThrow(id);
        proxy.setAccountsLinked(accountsLinked);

        proxyRepository.save(proxy);
    }

    public Proxy findByIdOrThrow(String id) {
        return proxyRepository.findById(id)
                .orElseThrow(() -> new ProxyAlreadyExistsException("Cannot find proxy by id " + id));
    }

    public void throwIfProxyExistsByUsername(String username) {
        proxyRepository.findByUsername(username)
                .ifPresent(proxy -> {
                    throw new ProxyAlreadyExistsException("Proxy already exists by username: " + username);
                });
    }

    private Proxy mapProxyRequestToProxy(AddProxyRequest.ProxyRequest proxyRequest) {
        return Proxy.builder()
                .username(proxyRequest.getUsername())
                .password(proxyRequest.getPassword())
                .host(proxyRequest.getHost())
                .port(proxyRequest.getPort())
                .rebootLink(proxyRequest.getRebootLink())
                .accountsLinked(0)
                .build();
    }
}
