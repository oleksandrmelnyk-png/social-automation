package com.kayleighrichmond.social_automation.system.client.ip_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.system.client.ip_api.dto.GetProxyAddressResponse;
import com.kayleighrichmond.social_automation.common.helper.OkHttpHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpApiClient {

    private final OkHttpHelper okHttpHelper;

    private final ObjectMapper objectMapper;

    private static final String IP_API_BASE_URL = "http://ip-api.com/json";

    public GetProxyAddressResponse getProxyAddress(Proxy proxy) {
        OkHttpClient client = okHttpHelper.buildClientWithProxy(proxy);

        Request request = new Request.Builder()
                .url(IP_API_BASE_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "IpApi error: " + response.code() + " - " + response.message());
            return objectMapper.readValue(responseBody, GetProxyAddressResponse.class);
        } catch (IOException e) {
            throw new IpApiException("Exception while getting address from proxy");
        }
    }
}
