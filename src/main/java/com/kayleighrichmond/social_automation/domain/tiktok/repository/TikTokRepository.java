package com.kayleighrichmond.social_automation.domain.tiktok.repository;

import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.common.type.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TikTokRepository extends JpaRepository<TikTokAccount, String> {

    List<TikTokAccount> findAllByStatus(Status status);

    void deleteAllByStatus(Status status);

    List<TikTokAccount> findAllByEmailIn(Collection<String> emails);
}
