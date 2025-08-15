package com.kayleighrichmond.social_automation.web.dto.tiktok;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.service.randomuser.dto.Dob;
import com.kayleighrichmond.social_automation.service.randomuser.dto.Name;
import com.kayleighrichmond.social_automation.type.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAccountRequest {

    private Name name;

    private String email;

    private String password;

    private Status status;

    private Dob dob;

    private String countryCode;

    private Proxy proxy;

}
