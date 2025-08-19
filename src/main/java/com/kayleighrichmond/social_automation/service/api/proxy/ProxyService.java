package com.kayleighrichmond.social_automation.service.api.proxy;

import com.kayleighrichmond.social_automation.domain.entity.Proxy;
import com.kayleighrichmond.social_automation.domain.repository.ProxyRepository;
import com.kayleighrichmond.social_automation.service.client.ip_api.IpApiClient;
import com.kayleighrichmond.social_automation.service.client.ip_api.dto.GetProxyAddressResponse;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.NoProxiesAvailableException;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.ProxyAlreadyExistsException;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.ProxyNotFoundException;
import com.kayleighrichmond.social_automation.service.api.proxy.mapper.ProxyMapper;
import com.kayleighrichmond.social_automation.web.controller.proxy.dto.AddProxyRequest;
import com.kayleighrichmond.social_automation.web.controller.proxy.dto.UpdateProxyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProxyService {

    private final ProxyRepository proxyRepository;

    private final IpApiClient ipApiClient;

    public List<Proxy> findAll() {
        return proxyRepository.findAll();
    }

    public List<Proxy> findAllByCountryCode(String countryCode) {
        return proxyRepository.findAllByCountryCode(countryCode);
    }

    public List<Proxy> findAllVerifiedByCountryCodeAndAccountsLinked(String countryCode, int accountsLinkedMax, int accountCount) {
        List<Proxy> proxies = proxyRepository.finaAllByCountryCodeVerifiedAndMaxAccounts(countryCode, true, accountsLinkedMax, accountCount);

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

            Proxy proxy = ProxyMapper.mapProxyRequestToProxy(proxyRequest);
            GetProxyAddressResponse proxyAddress = ipApiClient.getProxyAddress(proxy);
            proxy.setCountryCode(proxyAddress.getCountryCode());
            proxy.setAccountsLinked(0);

            proxies.add(proxy);
        }

        proxyRepository.saveAll(proxies);
    }

    public void update(String id, UpdateProxyRequest updateProxyRequest) {
        Proxy proxy = findByIdOrThrow(id);

        Optional.ofNullable(updateProxyRequest.getVerified()).ifPresent(proxy::setVerified);
        Optional.ofNullable(updateProxyRequest.getUsername()).ifPresent(proxy::setUsername);
        Optional.ofNullable(updateProxyRequest.getPassword()).ifPresent(proxy::setPassword);
        Optional.ofNullable(updateProxyRequest.getHost()).ifPresent(proxy::setHost);
        Optional.ofNullable(updateProxyRequest.getCountryCode()).ifPresent(proxy::setCountryCode);
        Optional.ofNullable(updateProxyRequest.getPort()).ifPresent(proxy::setPort);
        Optional.ofNullable(updateProxyRequest.getAccountsLinked()).ifPresent(proxy::setAccountsLinked);
        Optional.ofNullable(updateProxyRequest.getRebootLink()).ifPresent(proxy::setRebootLink);

        proxyRepository.save(proxy);
    }

    private Proxy findByIdOrThrow(String id) {
        return proxyRepository.findById(id)
                .orElseThrow(() -> new ProxyNotFoundException("Cannot find proxy by id " + id));
    }

    private void throwIfProxyExistsByUsername(String username) {
        proxyRepository.findByUsername(username)
                .ifPresent(proxy -> {
                    throw new ProxyAlreadyExistsException("Proxy already exists by username: " + username);
                });
    }
}
