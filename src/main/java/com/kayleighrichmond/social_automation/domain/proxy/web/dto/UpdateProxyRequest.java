package com.kayleighrichmond.social_automation.domain.proxy.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProxyRequest {

    private Boolean verified;

    private String username;

    private String password;

    private String host;

    private String countryCode;

    private Integer port;

    private Integer accountsLinked;

    private String rebootLink;

}
