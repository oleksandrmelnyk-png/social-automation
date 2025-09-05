package com.kayleighrichmond.social_automation.domain.proxy.repository;

import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProxyRepository extends JpaRepository<Proxy, String> {

    List<Proxy> findAllByCountryCodeAndVerified(String countryCode, boolean verified);

    List<Proxy> findAllByCountryCode(String countryCode);

    Optional<Proxy> findByUsername(String username);

}
