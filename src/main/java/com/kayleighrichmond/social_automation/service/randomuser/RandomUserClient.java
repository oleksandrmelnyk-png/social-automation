package com.kayleighrichmond.social_automation.service.randomuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kayleighrichmond.social_automation.service.okhttp.OkHttpHelper;
import com.kayleighrichmond.social_automation.service.randomuser.dto.RandomUserResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RandomUserClient {

    private final OkHttpHelper okHttpHelper;

    private final ObjectMapper objectMapper;

    private static final String RANDOM_USER_API = "https://randomuser.me/api/";

    public RandomUserResponse.RandomResult getRandomUser() {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request request = new Request.Builder()
                    .url(RANDOM_USER_API)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "RandomUserApi: " + response.code() + " - " + response.message());
            response.close();

            return objectMapper.readValue(responseBody, RandomUserResponse.class).getResults().get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
