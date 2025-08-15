package com.kayleighrichmond.social_automation.repository;

import com.kayleighrichmond.social_automation.model.Proxy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProxyRepository extends JpaRepository<Proxy, String> {

    @Query(value = "SELECT * FROM proxy " +
            "WHERE country_code = :countryCode " +
            "AND verified = :verified " +
            "AND accounts_linked < :maxAccounts " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Proxy> finaAllByCountryCodeVerifiedAndMaxAccounts(
            @Param("countryCode") String countryCode,
            @Param("verified") boolean verified,
            @Param("maxAccounts") int maxAccounts,
            @Param("limit") int limit
    );

    Optional<Proxy> findByUsername(String username);
}
