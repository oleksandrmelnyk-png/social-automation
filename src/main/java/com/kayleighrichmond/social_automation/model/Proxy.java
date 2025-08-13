package com.kayleighrichmond.social_automation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Proxy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private boolean verified;

    private String username;

    private String password;

    private String host;

    private String countryCode;

    private int port;

    private String rebootLink;

    private Integer accountsLinked;

}