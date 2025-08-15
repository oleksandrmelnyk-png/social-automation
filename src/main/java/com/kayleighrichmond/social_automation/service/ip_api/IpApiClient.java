package com.kayleighrichmond.social_automation.service.ip_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.service.ip_api.dto.GetProxyAddressResponse;
import com.kayleighrichmond.social_automation.service.okhttp.OkHttpHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpApiClient {

    private final OkHttpHelper okHttpHelper;

    private final ObjectMapper objectMapper;

    private final String IP_API_BASE_URL = "http://ip-api.com/json";

    public GetProxyAddressResponse getProxyAddress(Proxy proxy) {
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort())))
                .proxyAuthenticator((route, response) -> response.request().newBuilder()
                        .header("Proxy-Authorization", Credentials.basic(proxy.getUsername(), proxy.getPassword()))
                        .build())
                .build();

        Request request = new Request.Builder()
                .url(IP_API_BASE_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "IpApi error: " + response.code() + " - " + response.message());
            return objectMapper.readValue(responseBody, GetProxyAddressResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
