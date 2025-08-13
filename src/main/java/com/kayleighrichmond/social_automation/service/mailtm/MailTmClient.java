package com.kayleighrichmond.social_automation.service.mailtm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kayleighrichmond.social_automation.service.mailtm.dto.AccountRequest;
import com.kayleighrichmond.social_automation.service.mailtm.dto.GetDomainsResponse;
import com.kayleighrichmond.social_automation.service.mailtm.dto.GetMessagesResponse;
import com.kayleighrichmond.social_automation.service.mailtm.dto.GetTokenResponse;
import com.kayleighrichmond.social_automation.service.mailtm.exception.NoDomainFoundException;
import com.kayleighrichmond.social_automation.service.mailtm.exception.NoMessagesReceivedException;
import com.kayleighrichmond.social_automation.service.okhttp.OkHttpHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailTmClient {

    private final ObjectMapper objectMapper;

    private final OkHttpHelper okHttpHelper;

    private static final String MAIL_TM_API = "https://api.mail.tm";

    public void createAccount(String address, String password) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            String createAccountUrl = MAIL_TM_API + "/accounts";
            AccountRequest accountRequest = AccountRequest.builder()
                    .address(address)
                    .password(password)
                    .build();
            RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsString(accountRequest), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(createAccountUrl)
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            okHttpHelper.buildResponseBodyOrThrow(response, "MailTmApi error: " + response.code() + " - " + response.message());
            response.close();

            Thread.sleep(1100);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public GetTokenResponse getToken(String address, String password) {

        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            String createAccountUrl = MAIL_TM_API + "/token";
            AccountRequest accountRequest = AccountRequest.builder()
                    .address(address)
                    .password(password)
                    .build();
            RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsString(accountRequest), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(createAccountUrl)
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "MailTmApi error: " + response.code() + " - " + response.message());
            response.close();

            return objectMapper.readValue(responseBody, GetTokenResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GetDomainsResponse getDomains() {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            String createAccountUrl = MAIL_TM_API + "/domains";
            Request request = new Request.Builder()
                    .url(createAccountUrl)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "MailTmApi error: " + response.code() + " - " + response.message());
            response.close();

            GetDomainsResponse getDomainsResponse = objectMapper.readValue(responseBody, GetDomainsResponse.class);
            if (getDomainsResponse.getTotalItems() == 0) {
                throw new NoDomainFoundException("No domain found in MailTm");
            }

            return getDomainsResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GetMessagesResponse waitForMessages(String token) {
        int attempts = 0;

        for (int i = 0; i < 10; i++) {
            GetMessagesResponse messages = getMessages(token);
            if (messages.getTotalItems() == 0) {
                try {
                    attempts++;
                    Thread.sleep(3 * 1500);

                    log.info("Waiting for email message... " + attempts + "/" + 10);

                    continue;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
            return messages;
        }

        throw new NoMessagesReceivedException("No messages received for token: " + token);
    }

    private GetMessagesResponse getMessages(String token) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            String createAccountUrl = MAIL_TM_API + "/messages";
            Request request = new Request.Builder()
                    .url(createAccountUrl)
                    .header("Authorization", "Bearer " + token)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "MailTmApi error: " + response.code() + " - " + response.message());
            response.close();

            return objectMapper.readValue(responseBody, GetMessagesResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
