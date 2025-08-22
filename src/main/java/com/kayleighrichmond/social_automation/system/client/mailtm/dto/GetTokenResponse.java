package com.kayleighrichmond.social_automation.system.client.mailtm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetTokenResponse {

    private String token;

}
