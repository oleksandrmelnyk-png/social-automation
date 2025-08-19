package com.kayleighrichmond.social_automation.web.controller.tiktok.dto;

import com.kayleighrichmond.social_automation.domain.entity.Proxy;
import com.kayleighrichmond.social_automation.domain.entity.account.embedded.Dob;
import com.kayleighrichmond.social_automation.domain.entity.account.embedded.Name;
import com.kayleighrichmond.social_automation.domain.type.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAccountRequest {

    private Name name;

    private String email;

    private String password;

    private Status status;

    private String executionMessage;

    private Dob dob;

    private String countryCode;

    private Proxy proxy;

    private String nstProfileId;

}
