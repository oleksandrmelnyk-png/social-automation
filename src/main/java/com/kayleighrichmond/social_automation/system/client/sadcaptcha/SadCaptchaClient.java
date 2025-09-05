package com.kayleighrichmond.social_automation.system.client.sadcaptcha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.common.helper.OkHttpHelper;
import com.kayleighrichmond.social_automation.system.client.sadcaptcha.dto.RotateCaptchaRequest;
import com.kayleighrichmond.social_automation.system.client.sadcaptcha.dto.RotateCaptchaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SadCaptchaClient {

    private final OkHttpHelper okHttpHelper;

    private final OkHttpClient okHttpClient;

    private final ObjectMapper objectMapper;

    private static final String SAD_CAPTCHA_API_BASE_URL = "https://www.sadcaptcha.com/api/v1";

    @Value("${sad-captcha.api-key}")
    private String SAD_CAPTCHA_API_KEY;

    public RotateCaptchaResponse rotate(String outerImageBase, String innerImageBase) {
        try {
            String solveRotateCaptchaUrl = SAD_CAPTCHA_API_BASE_URL + "/rotate?licenseKey=" + SAD_CAPTCHA_API_KEY;
            RotateCaptchaRequest rotateCaptchaRequest = RotateCaptchaRequest.builder()
                    .outerImageB64(outerImageBase)
                    .innerImageB64(innerImageBase)
                    .build();

            RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsString(rotateCaptchaRequest), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(solveRotateCaptchaUrl)
                    .post(requestBody)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "SadCaptcha error: " + response.code() + " - " + response.message());
            response.close();

            return objectMapper.readValue(responseBody, RotateCaptchaResponse.class);
        } catch (IOException e) {
            throw new ServerException("Exception while solving rotation captcha in SadCaptcha");
        }
    }

}
