package com.kayleighrichmond.social_automation.service.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class OkHttpHelper {

    public String buildResponseBodyOrThrow(Response response, String message) throws IOException {
        String responseBody = response.body() != null ? response.body().string() : "";
        if (!response.isSuccessful()) {
            log.error("Failed to proceed request: {} - {}, Body: {}", response.code(), response.message(), responseBody);
            throw new IOException(message);
        }
        return responseBody;
    }
}
