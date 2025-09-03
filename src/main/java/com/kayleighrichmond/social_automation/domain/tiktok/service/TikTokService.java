package com.kayleighrichmond.social_automation.domain.tiktok.service;

import com.kayleighrichmond.social_automation.common.exception.AccountNotFoundException;
import com.kayleighrichmond.social_automation.common.exception.AccountsCurrentlyCreatingException;
import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.repository.TikTokRepository;
import com.kayleighrichmond.social_automation.common.type.Status;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TikTokService {

    private final TikTokRepository tikTokRepository;

    public TikTokAccount findById(String id) {
        return tikTokRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Not found tik tok account by id " + id));
    }

    public List<TikTokAccount> findAll() {
        return tikTokRepository.findAll();
    }

    public List<TikTokAccount> findAllByStatus(Status status) {
        return tikTokRepository.findAllByStatus(status);
    }

    public List<TikTokAccount> findAllByAction(Action action) {
        return tikTokRepository.findAllByAction(action);
    }

    public void saveAll(List<TikTokAccount> tikTokAccounts) {
        tikTokRepository.saveAll(retrieveNotExistingAccounts(tikTokAccounts));
    }

    public List<TikTokAccount> saveAllOrThrow(List<TikTokAccount> tikTokAccounts) {
        List<TikTokAccount> tikTokAccountsInProgress = findAllByStatus(Status.IN_PROGRESS);

        if (!tikTokAccountsInProgress.isEmpty()) {
            throw new AccountsCurrentlyCreatingException("Other accounts currently creating");
        }

        return tikTokRepository.saveAll(retrieveNotExistingAccounts(tikTokAccounts));
    }

    public void update(String id, UpdateAccountRequest updateAccountRequest) {
        TikTokAccount tikTokAccount = findById(id);

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
        Optional.ofNullable(updateAccountRequest.getCommentedPosts()).ifPresent(tikTokAccount::setCommentedPosts);
        Optional.ofNullable(updateAccountRequest.getPublishedPosts()).ifPresent(tikTokAccount::setPublishedPosts);
        Optional.ofNullable(updateAccountRequest.getAccountLink()).ifPresent(tikTokAccount::setAccountLink);

        tikTokRepository.save(tikTokAccount);
    }

    public void deleteAllByStatus(Status status) {
        tikTokRepository.deleteAllByStatus(status);
    }

    private List<TikTokAccount> retrieveNotExistingAccounts(List<TikTokAccount> tikTokAccounts) {
        List<String> emails = tikTokAccounts.stream()
                .map(TikTokAccount::getEmail)
                .filter(Objects::nonNull)
                .toList();

        Set<String> existingEmails = new HashSet<>(tikTokRepository.findAllByEmailIn(emails)
                .stream()
                .map(TikTokAccount::getEmail)
                .toList());

        return tikTokAccounts.stream()
                .filter(acc -> !existingEmails.contains(acc.getEmail()))
                .toList();
    }
}
