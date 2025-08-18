package com.kayleighrichmond.social_automation.service.client.randomuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kayleighrichmond.social_automation.service.http.OkHttpHelper;
import com.kayleighrichmond.social_automation.service.client.randomuser.dto.RandomUserResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RandomUserClient {

    private static final String RANDOM_USER_API = "https://randomuser.me/api/";

    private final OkHttpHelper okHttpHelper;

    private final OkHttpClient okHttpClient;

    private final ObjectMapper objectMapper;


    public RandomUserResponse.RandomResult getRandomUser() {
        try {
            Request request = new Request.Builder()
                    .url(RANDOM_USER_API)
                    .get()
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "RandomUserApi: " + response.code() + " - " + response.message());
            response.close();

            return objectMapper.readValue(responseBody, RandomUserResponse.class).getResults().get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
