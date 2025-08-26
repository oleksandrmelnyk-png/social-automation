package com.kayleighrichmond.social_automation.domain.proxy.repository;

import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProxyRepository extends JpaRepository<Proxy, String> {

    @Query(value = "SELECT * FROM proxy " +
            "WHERE country_code = :countryCode " +
            "AND verified = :verified " +
            "AND accounts_linked < :accountsLinkedMax " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Proxy> findAllByCountryCodeVerifiedAccountsLimit(
            @Param("countryCode") String countryCode,
            @Param("verified") boolean verified,
            @Param("accountsLinkedMax") int accountsLinkedMax,
            @Param("limit") int limit
    );

    List<Proxy> findAllByCountryCodeAndVerified(String countryCode, boolean verified);

    Optional<Proxy> findByUsername(String username);

}
