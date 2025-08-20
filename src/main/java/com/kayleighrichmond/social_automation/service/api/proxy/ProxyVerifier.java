package com.kayleighrichmond.social_automation.service.api.proxy;

import com.kayleighrichmond.social_automation.domain.entity.Proxy;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.ProxyRotationFailed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyVerifier {

    private static final String IF_CONFIG_BASE_URL = "https://ifconfig.me/ip";

    public boolean verifyProxy(Proxy proxy, boolean changeIp) {
        int maxVerifyTries = 3;

        if (changeIp && proxy.getRebootLink() != null) {
            changeProxyIp(proxy.getRebootLink());
            log.info("Updated proxy ip for {}", proxy.getUsername());
        }

        int attempts = 1;
        try {
            for (int i = 0; i < maxVerifyTries; i++) {
                log.info("Trying to verify proxy {} ... {}", proxy.getUsername(), "%d/%d".formatted(attempts++, maxVerifyTries));
                boolean validProxy = processProxyValidation(proxy);
                if (validProxy) {
                    return true;
                }

                Thread.sleep(1500);
            }
        } catch (InterruptedException | IllegalArgumentException e) {
            log.warn("Failed proxy verification {}", proxy.getUsername());
            return false;
        }
        log.warn("Failed proxy verification {}", proxy.getUsername());
        return false;
    }

    private boolean processProxyValidation(Proxy proxy) {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(3, TimeUnit.SECONDS)
                .proxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort())))
                .proxyAuthenticator((route, response) -> response.request().newBuilder()
                        .header("Proxy-Authorization", Credentials.basic(proxy.getUsername(), proxy.getPassword()))
                        .build())
                .build();

        Request request = new Request.Builder()
                .url(IF_CONFIG_BASE_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful() && response.body() != null;
        } catch (IOException e) {
            return false;
        }
    }

    public void changeProxyIp(String rebootLink) {
        if (rebootLink == null) {
            throw new IllegalArgumentException("RebootLink cannot be null");
        }

        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(rebootLink)
                .build();

        try (Response ignored = client.newCall(request).execute()) {
        } catch (IOException e) {
            log.warn("Failed proxy rotation: {}", e.getMessage());
            throw new ProxyRotationFailed("Failed proxy rotation ip " + rebootLink);
        }
    }
}
