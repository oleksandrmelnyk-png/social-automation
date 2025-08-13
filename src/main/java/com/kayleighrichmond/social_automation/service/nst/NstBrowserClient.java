package com.kayleighrichmond.social_automation.service.nst;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.service.nst.dto.CreateProfileRequest;
import com.kayleighrichmond.social_automation.service.nst.dto.CreateProfileResponse;
import com.kayleighrichmond.social_automation.service.nst.dto.StartBrowserResponse;
import com.kayleighrichmond.social_automation.service.nst.exception.NstBrowserException;
import com.kayleighrichmond.social_automation.service.okhttp.OkHttpHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static com.kayleighrichmond.social_automation.service.nst.dto.builder.CreateProfileRequestBuilder.buildCreateProfileRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class NstBrowserClient {

    private final ObjectMapper objectMapper;

    private final OkHttpHelper okHttpHelper;

    @Value("${nst-browser.api-key}")
    private String NST_BROWSER_API_KEY;

    @Value("${nst-browser.url}")
    private String NST_API;

    public CreateProfileResponse createProfile(String profileName, Proxy proxy) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            CreateProfileRequest createProfileRequest = buildCreateProfileRequest(profileName, proxy);

            String json = objectMapper.writeValueAsString(createProfileRequest);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);

            Request request = new Request.Builder()
                    .url(NST_API + "/profiles")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", NST_BROWSER_API_KEY)
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.warn("Response of creating nst profile for proxy {} failed", proxy);
            }

            CreateProfileResponse createProfileResponse = objectMapper.readValue(Objects.requireNonNull(response.body()).string(), CreateProfileResponse.class);

            response.close();
            return createProfileResponse;
        } catch (IOException e) {
            throw new NstBrowserException("Couldn't create profile in Nst browser");
        }
    }

    public StartBrowserResponse startBrowser(String profileId) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");

        Request request = new Request.Builder()
                .url(NST_API + "/browsers/" + profileId)
                .method("POST", body)
                .addHeader("x-api-key", NST_BROWSER_API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()){
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "NstApi error: " + response.code() + " - " + response.message());
            StartBrowserResponse startBrowserResponse = objectMapper.readValue(responseBody, StartBrowserResponse.class);

            response.close();
            return startBrowserResponse;
        } catch (IOException e) {
            throw new NstBrowserException("Couldn't start Nst browser");
        }
    }

    public void deleteBrowser(String profileId) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");

        Request request = new Request.Builder()
                .url(NST_API + "/profiles/" + profileId)
                .method("DELETE", body)
                .addHeader("x-api-key", NST_BROWSER_API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()){
            okHttpHelper.buildResponseBodyOrThrow(response, "NstApi error: " + response.code() + " - " + response.message());
        } catch (IOException e) {
            throw new NstBrowserException("Couldn't delete Nst profile");
        }
    }
}
