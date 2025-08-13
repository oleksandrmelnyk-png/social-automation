package com.kayleighrichmond.social_automation.service.randomuser.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomUserResponse {

    private List<RandomResult> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RandomResult {

        private String gender;

        @JsonIgnoreProperties(ignoreUnknown = true)
        private Name name;

        private String email;

        private Dob dob;

    }
}
