package com.kayleighrichmond.social_automation.service.mailtm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetDomainsResponse {

    @JsonProperty("hydra:totalItems")
    private int totalItems;

    @JsonProperty("hydra:member")
    private List<Domain> domains;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Domain {

        private String id;

        private String domain;

        @JsonProperty("isActive")
        private boolean isActive;
    }
}
