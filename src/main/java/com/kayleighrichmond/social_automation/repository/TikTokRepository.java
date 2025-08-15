package com.kayleighrichmond.social_automation.repository;

import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.type.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TikTokRepository extends JpaRepository<TikTokAccount, String> {

    Optional<TikTokAccount> findByStatus(Status status);

    Optional<TikTokAccount> findByEmail(String email);

    List<TikTokAccount> findAllByStatus(Status status);

    void deleteAllByStatus(Status status);

    List<TikTokAccount> findAllByIdIn(Collection<String> ids);
}
