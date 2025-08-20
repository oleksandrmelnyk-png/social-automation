package com.kayleighrichmond.social_automation.system.client.ip_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetProxyAddressResponse {

   private String country;

   private String countryCode;

   private String city;

   private int lat;

   private int lon;
}
