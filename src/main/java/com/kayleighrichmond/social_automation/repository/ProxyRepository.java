package com.kayleighrichmond.social_automation.repository;

import com.kayleighrichmond.social_automation.model.Proxy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProxyRepository extends JpaRepository<Proxy, String> {

    List<Proxy> findAllByCountryCodeAndVerifiedAndAccountsLinkedLessThan(String countryCode, boolean verified, int accountsLinkedIsLessThan, Pageable pageable);

    Optional<Proxy> findByUsername(String username);
}
