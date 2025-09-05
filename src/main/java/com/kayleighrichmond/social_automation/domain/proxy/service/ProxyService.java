package com.kayleighrichmond.social_automation.domain.proxy.service;

import com.kayleighrichmond.social_automation.common.helper.ProxyHelper;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.ProxyNotAvailableException;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.proxy.repository.ProxyRepository;
import com.kayleighrichmond.social_automation.system.client.ip_api.IpApiClient;
import com.kayleighrichmond.social_automation.system.client.ip_api.dto.GetProxyAddressResponse;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.ProxyAlreadyExistsException;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.ProxyNotFoundException;
import com.kayleighrichmond.social_automation.domain.proxy.common.mapper.ProxyMapper;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.AddProxyRequest;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.UpdateProxyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProxyService {

    private final IpApiClient ipApiClient;

    private final ProxyMapper proxyMapper;

    private final ProxyRepository proxyRepository;

    private final ProxyHelper proxyHelper;

    private final ProxyVerifier proxyVerifier;

    public List<Proxy> findAll() {
        return proxyRepository.findAll();
    }

    public void saveAll(AddProxyRequest addProxyRequest) {
        List<Proxy> proxies = new ArrayList<>();
        List<AddProxyRequest.ProxyRequest> proxyRequests = addProxyRequest.getProxies();

        for (AddProxyRequest.ProxyRequest proxyRequest : proxyRequests) {
            Proxy proxy = proxyMapper.mapDtoToEntity(proxyRequest);
            proxyHelper.verifyOrThrow(proxy);
        }

        for (AddProxyRequest.ProxyRequest proxyRequest : proxyRequests) {
            throwIfProxyExistsByUsername(proxyRequest.getUsername());

            Proxy proxy = proxyMapper.mapDtoToEntity(proxyRequest);
            GetProxyAddressResponse proxyAddress = ipApiClient.getProxyAddress(proxy);
            proxy.setCountryCode(proxyAddress.getCountryCode());
            proxy.setAccountsLinked(0);
            proxy.setVerified(true);
            proxy.setAutoRotateInterval(proxyRequest.getAutoRotateInterval() * 1000);
            proxy.setLastRotation(Instant.now());

            proxies.add(proxy);
        }

        proxyRepository.saveAll(proxies);
    }

    public List<Proxy> verifyAllByCountryCode(String countryCode) {
        List<Proxy> proxiesByCountryCode = proxyRepository.findAllByCountryCode(countryCode);
        List<Proxy> verifiedProxies = new ArrayList<>();

        for (Proxy proxy : proxiesByCountryCode) {
            boolean verifiedProxy = proxyVerifier.verifyProxy(proxy, false);
            if (verifiedProxy) {
                verifiedProxies.add(proxy);
            }

            if (verifiedProxy && !proxy.isVerified()) {
                update(proxy.getId(), UpdateProxyRequest.builder().verified(true).build());
            } else if (!verifiedProxy && proxy.isVerified()) {
                update(proxy.getId(), UpdateProxyRequest.builder().verified(false).build());
            }
        }

        if (verifiedProxies.isEmpty()) {
            throw new ProxyNotAvailableException("No proxies available by country code " + countryCode);
        }

        return verifiedProxies;
    }

    public List<Proxy> findAllByCountryCodeAndVerified(String countryCode, boolean verified) {
        return proxyRepository.findAllByCountryCodeAndVerified(countryCode, verified);
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
        Optional.ofNullable(updateProxyRequest.getLastRotation()).ifPresent(proxy::setLastRotation);

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
