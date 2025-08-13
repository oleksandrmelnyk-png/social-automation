package com.kayleighrichmond.social_automation.service.account.tiktok;

import com.kayleighrichmond.social_automation.exception.AccountNotFoundException;
import com.kayleighrichmond.social_automation.exception.AccountsCurrentlyCreatingException;
import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.repository.TikTokRepository;
import com.kayleighrichmond.social_automation.type.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TikTokService {

    private final TikTokRepository tikTokRepository;

    public void save(TikTokAccount tikTokAccount) {
        throwIfExistsByEmail(tikTokAccount.getEmail());
        tikTokRepository.save(tikTokAccount);
    }

    public void update(TikTokAccount tikTokAccount) {
        throwIfNotExistsByEmail(tikTokAccount.getEmail());
        tikTokRepository.save(tikTokAccount);
    }

    public List<TikTokAccount> findAllByStatus(Status status) {
        return tikTokRepository.findAllByStatus(status);
    }

    public List<TikTokAccount> findAll() {
        return tikTokRepository.findAll();
    }

    @Transactional
    public void updateAllFromInProgressToFailed() {
        List<TikTokAccount> allTikTokAccountsInProgress = findAllByStatus(Status.IN_PROGRESS);

        for (TikTokAccount tikTokAccountsInProgress : allTikTokAccountsInProgress) {
            tikTokAccountsInProgress.setStatus(Status.FAILED);
        }

        tikTokRepository.saveAll(allTikTokAccountsInProgress);
    }

    public void throwIfAccountsInProgressExists(Status status) {
        Optional<TikTokAccount> tikTokAccount = tikTokRepository.findByStatus(status);
        if (tikTokAccount.isPresent()) {
            throw new AccountsCurrentlyCreatingException("Other accounts currently creating");
        }
    }

    private void throwIfExistsByEmail(String email) {
        tikTokRepository.findByEmail(email)
                .ifPresent((tikTokAccount) -> {
                    throw new AccountNotFoundException("Not found tik tok account by email " + email);
                });
    }

    private void throwIfNotExistsByEmail(String email) {
         tikTokRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Not found tik tok account by email " + email));
    }
}
