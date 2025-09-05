package com.kayleighrichmond.social_automation.system.client.mailtm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.system.client.mailtm.dto.AccountRequest;
import com.kayleighrichmond.social_automation.system.client.mailtm.dto.GetDomainsResponse;
import com.kayleighrichmond.social_automation.system.client.mailtm.dto.GetMessagesResponse;
import com.kayleighrichmond.social_automation.system.client.mailtm.dto.GetTokenResponse;
import com.kayleighrichmond.social_automation.common.helper.OkHttpHelper;
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

    private final OkHttpClient okHttpClient;

    private static final String MAIL_TM_BASE_URL= "https://api.mail.tm";

    public void createAccount(String address, String password) {
        try {
            String createAccountUrl = MAIL_TM_BASE_URL + "/accounts";
            AccountRequest accountRequest = AccountRequest.builder()
                    .address(address)
                    .password(password)
                    .build();

            RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsString(accountRequest), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(createAccountUrl)
                    .post(requestBody)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            okHttpHelper.buildResponseBodyOrThrow(response, "MailTmApi error: " + response.code() + " - " + response.message());
            response.close();

            Thread.sleep(1100);
        } catch (IOException | InterruptedException e) {
            throw new ServerException("Exception while creating account in MailTm");
        }
    }

    public GetTokenResponse getToken(String address, String password) {
        try {
            String createAccountUrl = MAIL_TM_BASE_URL + "/token";
            AccountRequest accountRequest = AccountRequest.builder()
                    .address(address)
                    .password(password)
                    .build();

            RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsString(accountRequest), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(createAccountUrl)
                    .post(requestBody)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "MailTmApi error: " + response.code() + " - " + response.message());
            response.close();

            return objectMapper.readValue(responseBody, GetTokenResponse.class);
        } catch (IOException e) {
            throw new ServerException("Exception while getting token from MailTm");
        }
    }

    public GetDomainsResponse getDomains() {
        try {
            String createAccountUrl = MAIL_TM_BASE_URL + "/domains";
            Request request = new Request.Builder()
                    .url(createAccountUrl)
                    .get()
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "MailTmApi error: " + response.code() + " - " + response.message());
            response.close();

            GetDomainsResponse getDomainsResponse = objectMapper.readValue(responseBody, GetDomainsResponse.class);
            if (getDomainsResponse.getTotalItems() == 0) {
                throw new ServerException("No domain found in MailTm");
            }

            return getDomainsResponse;
        } catch (IOException e) {
            throw new ServerException("Exception while getting domains from MailTm");
        }
    }

    public GetMessagesResponse getMessages(String token) {
        try {
            String createAccountUrl = MAIL_TM_BASE_URL + "/messages";
            Request request = new Request.Builder()
                    .url(createAccountUrl)
                    .header("Authorization", "Bearer " + token)
                    .get()
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "MailTmApi error: " + response.code() + " - " + response.message());
            response.close();

            return objectMapper.readValue(responseBody, GetMessagesResponse.class);
        } catch (IOException e) {
            throw new ServerException("Exception while getting messages from MailTm");
        }
    }
}
