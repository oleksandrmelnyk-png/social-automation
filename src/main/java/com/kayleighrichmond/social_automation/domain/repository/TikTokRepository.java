package com.kayleighrichmond.social_automation.domain.repository;

import com.kayleighrichmond.social_automation.domain.entity.account.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.type.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TikTokRepository extends JpaRepository<TikTokAccount, String> {

    Optional<TikTokAccount> findByStatus(Status status);

    List<TikTokAccount> findAllByStatus(Status status);

    void deleteAllByStatus(Status status);

    List<TikTokAccount> findAllByEmailIn(Collection<String> emails);
}
