package com.kayleighrichmond.social_automation.service.client.mailtm.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountRequest {

    private String address;

    private String password;

}
