package com.kayleighrichmond.social_automation.service.account.tiktok;

import com.kayleighrichmond.social_automation.exception.AccountNotFoundException;
import com.kayleighrichmond.social_automation.exception.AccountsCurrentlyCreatingException;
import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.repository.TikTokRepository;
import com.kayleighrichmond.social_automation.type.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TikTokService {

    private final TikTokRepository tikTokRepository;

    public void save(TikTokAccount tikTokAccount) {
        throwIfExists(tikTokAccount.getEmail());
        tikTokRepository.save(tikTokAccount);
    }

    public void update(TikTokAccount tikTokAccount) {
        throwIfNotExistsByEmail(tikTokAccount.getEmail());
        tikTokRepository.save(tikTokAccount);
    }

    public List<TikTokAccount> findAll() {
        return tikTokRepository.findAll();
    }

    public void throwIfAccountsInProgressExists(Status status) {
        boolean existsByStatus = tikTokRepository.existsByStatus(status);
        if (existsByStatus) {
            throw new AccountsCurrentlyCreatingException("Other accounts currently creating");
        }
    }

    public void throwIfExists(String email) {
        tikTokRepository.findByEmail(email)
                .ifPresent((tikTokAccount) -> {
                    throw new AccountNotFoundException("Not found tik tok account by email " + email);
                });
    }

    public void throwIfNotExistsByEmail(String email) {
         tikTokRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Not found tik tok account by email " + email));
    }
}
