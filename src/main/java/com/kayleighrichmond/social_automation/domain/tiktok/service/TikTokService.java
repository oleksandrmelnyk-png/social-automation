package com.kayleighrichmond.social_automation.domain.tiktok.service;

import com.kayleighrichmond.social_automation.common.exception.AccountNotFoundException;
import com.kayleighrichmond.social_automation.common.exception.AccountsCurrentlyCreatingException;
import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokBaseAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.repository.TikTokRepository;
import com.kayleighrichmond.social_automation.common.type.Status;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TikTokService {

    private final TikTokRepository tikTokRepository;

    public TikTokBaseAccount findById(String id) {
        return tikTokRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Not found tik tok account by id " + id));
    }

    public List<TikTokBaseAccount> findAll() {
        return tikTokRepository.findAll();
    }

    public List<TikTokBaseAccount> findAllByStatus(Status status) {
        return tikTokRepository.findAllByStatus(status);
    }

    public List<TikTokBaseAccount> saveAll(List<TikTokBaseAccount> tikTokAccounts) {
        throwIfAccountsInProgressExists(Status.IN_PROGRESS);
        return tikTokRepository.saveAll(retrieveNotExistingAccounts(tikTokAccounts));
    }

    public void update(String id, UpdateAccountRequest updateAccountRequest) {
        TikTokBaseAccount tikTokAccount = findById(id);

        Optional.ofNullable(updateAccountRequest.getName()).ifPresent(tikTokAccount::setName);
        Optional.ofNullable(updateAccountRequest.getEmail()).ifPresent(tikTokAccount::setEmail);
        Optional.ofNullable(updateAccountRequest.getPassword()).ifPresent(tikTokAccount::setPassword);
        Optional.ofNullable(updateAccountRequest.getStatus()).ifPresent(tikTokAccount::setStatus);
        Optional.ofNullable(updateAccountRequest.getDob()).ifPresent(tikTokAccount::setDob);
        Optional.ofNullable(updateAccountRequest.getCountryCode()).ifPresent(tikTokAccount::setCountryCode);
        Optional.ofNullable(updateAccountRequest.getProxy()).ifPresent(tikTokAccount::setProxy);
        Optional.ofNullable(updateAccountRequest.getExecutionMessage()).ifPresent(tikTokAccount::setExecutionMessage);
        Optional.ofNullable(updateAccountRequest.getNstProfileId()).ifPresent(tikTokAccount::setNstProfileId);
        Optional.ofNullable(updateAccountRequest.getAction()).ifPresent(tikTokAccount::setAction);
        Optional.ofNullable(updateAccountRequest.getLikedPosts()).ifPresent(tikTokAccount::setLikedPosts);

        tikTokRepository.save(tikTokAccount);
    }

    @Transactional
    public void updateAllFromCreationStatusInProgressToFailed(String executionMessage) {
        List<TikTokBaseAccount> allTikTokAccountsInProgress = findAllByStatus(Status.IN_PROGRESS);

        for (TikTokBaseAccount tikTokAccountsInProgress : allTikTokAccountsInProgress) {
            tikTokAccountsInProgress.setStatus(Status.FAILED);
            tikTokAccountsInProgress.setExecutionMessage(executionMessage);
        }

        tikTokRepository.saveAll(allTikTokAccountsInProgress);
    }

    public void updateFromActionStatusInProgressToFailedById(String accountId, String executionMessage) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.FAILED)
                .executionMessage(executionMessage)
                .build();

        update(accountId, updateAccountRequest);
    }

    public void deleteAllByStatus(Status status) {
        tikTokRepository.deleteAllByStatus(status);
    }

    private List<TikTokBaseAccount> retrieveNotExistingAccounts(List<TikTokBaseAccount> tikTokAccounts) {
        List<String> emails = tikTokAccounts.stream()
                .map(TikTokBaseAccount::getEmail)
                .filter(Objects::nonNull)
                .toList();

        Set<String> existingEmails = new HashSet<>(tikTokRepository.findAllByEmailIn(emails)
                .stream()
                .map(TikTokBaseAccount::getEmail)
                .toList());

        return tikTokAccounts.stream()
                .filter(acc -> !existingEmails.contains(acc.getEmail()))
                .toList();
    }

    private void throwIfAccountsInProgressExists(Status status) {
        List<TikTokBaseAccount> tikTokAccounts = findAllByStatus(status);
        if (!tikTokAccounts.isEmpty()) {
            throw new AccountsCurrentlyCreatingException("Other accounts currently creating");
        }
    }
}
