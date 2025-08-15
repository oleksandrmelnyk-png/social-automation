package com.kayleighrichmond.social_automation.service.proxy;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.service.proxy.exception.ProxyRotationFailed;
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
public class ProxyVerifier {

    public boolean verifyProxy(Proxy proxy, boolean changeIp) {
        if (changeIp && proxy.getRebootLink() != null) {
            changeProxyIp(proxy.getRebootLink());
            log.info("Updated proxy ip for {}", proxy.getUsername());
        }

        int attempts = 1;

        try {
            for (int i = 0; i < 5; i++) {
                log.info("Trying to verify proxy {} ... {}", proxy.getUsername(), "%d/5".formatted(attempts++));
                boolean validProxy = processProxyValidation(proxy);
                if (validProxy) {
                    return true;
                }

                Thread.sleep(1500);
            }
        } catch (InterruptedException | IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    private boolean processProxyValidation(Proxy proxy) {
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort())))
                .proxyAuthenticator((route, response) -> response.request().newBuilder()
                        .header("Proxy-Authorization", Credentials.basic(proxy.getUsername(), proxy.getPassword()))
                        .build())
                .build();

        Request request = new Request.Builder()
                .url("https://ifconfig.me/ip")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return true;
            } else {
                return false;
            }
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
