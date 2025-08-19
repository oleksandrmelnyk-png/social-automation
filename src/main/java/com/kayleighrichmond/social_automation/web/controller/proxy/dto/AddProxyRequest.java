package com.kayleighrichmond.social_automation.web.controller.proxy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddProxyRequest {

    @NotNull(message = "Proxies is required")
    private List<ProxyRequest> proxies;

    @Data
    public static class ProxyRequest {

        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        @NotBlank(message = "Host is required")
        private String host;

        @NotNull(message = "Port is required")
        private Integer port;

        private String rebootLink;
    }

}
