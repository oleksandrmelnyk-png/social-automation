package com.kayleighrichmond.social_automation.service.account.tiktok;

import com.kayleighrichmond.social_automation.service.account.exception.AccountNotFoundException;
import com.kayleighrichmond.social_automation.service.account.exception.AccountsCurrentlyCreatingException;
import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.repository.TikTokRepository;
import com.kayleighrichmond.social_automation.type.Status;
import com.kayleighrichmond.social_automation.web.dto.tiktok.UpdateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TikTokService {

    private final TikTokRepository tikTokRepository;

    public List<TikTokAccount> saveAll(List<TikTokAccount> tikTokAccounts) {
        List<String> ids = tikTokAccounts.stream()
                .map(TikTokAccount::getId)
                .filter(Objects::nonNull)
                .toList();

        Set<String> existingIds = new HashSet<>(tikTokRepository.findAllByIdIn(ids)
                .stream()
                .map(TikTokAccount::getId)
                .toList());

        List<TikTokAccount> newAccounts = tikTokAccounts.stream()
                .filter(acc -> acc.getId() == null || !existingIds.contains(acc.getId()))
                .toList();

        return tikTokRepository.saveAll(newAccounts);
    }

    public void update(String id, UpdateAccountRequest updateAccountRequest) {
        TikTokAccount tikTokAccount = findByIdOrThrow(id);

        Optional.ofNullable(updateAccountRequest.getName()).ifPresent(tikTokAccount::setName);
        Optional.ofNullable(updateAccountRequest.getEmail()).ifPresent(tikTokAccount::setEmail);
        Optional.ofNullable(updateAccountRequest.getPassword()).ifPresent(tikTokAccount::setPassword);
        Optional.ofNullable(updateAccountRequest.getStatus()).ifPresent(tikTokAccount::setStatus);
        Optional.ofNullable(updateAccountRequest.getDob()).ifPresent(tikTokAccount::setDob);
        Optional.ofNullable(updateAccountRequest.getCountryCode()).ifPresent(tikTokAccount::setCountryCode);
        Optional.ofNullable(updateAccountRequest.getProxy()).ifPresent(tikTokAccount::setProxy);

        tikTokRepository.save(tikTokAccount);
    }

    public List<TikTokAccount> findAllByStatus(Status status) {
        return tikTokRepository.findAllByStatus(status);
    }

    public List<TikTokAccount> findAll() {
        return tikTokRepository.findAll();
    }

    @Transactional
    public void deleteAllByStatus(Status status) {
        tikTokRepository.deleteAllByStatus(status);
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

    private TikTokAccount findByIdOrThrow(String id) {
        return tikTokRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Not found tik tok account by id " + id));
    }
}
