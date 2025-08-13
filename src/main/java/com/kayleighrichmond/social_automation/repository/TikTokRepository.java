package com.kayleighrichmond.social_automation.repository;

import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.type.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TikTokRepository extends JpaRepository<TikTokAccount, String> {

    boolean existsByStatus(Status status);

    Optional<TikTokAccount> findByEmail(String email);
}
