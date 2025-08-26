package com.kayleighrichmond.social_automation.domain.tiktok.web.dto;

import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.tiktok.model.embedded.Dob;
import com.kayleighrichmond.social_automation.domain.tiktok.model.embedded.Name;
import com.kayleighrichmond.social_automation.common.type.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAccountRequest {

    private Name name;

    private String email;

    private String password;

    private Status status;

    private Action action;

    private Integer likedPosts;

    private Integer commentedPosts;

    private Integer publishedPosts;

    private String executionMessage;

    private Dob dob;

    private String countryCode;

    private Proxy proxy;

    private String nstProfileId;

}
