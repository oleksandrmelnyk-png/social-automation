package com.kayleighrichmond.social_automation.model;

import com.kayleighrichmond.social_automation.service.randomuser.dto.Dob;
import com.kayleighrichmond.social_automation.service.randomuser.dto.Name;
import com.kayleighrichmond.social_automation.type.Status;
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
public class TikTokAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String countryCode;

    @Embedded
    private Name name;

    private String username;

    @Embedded
    private Dob dob;

    private String accountLink;

    @ManyToOne
    @JoinColumn(name = "proxy_id")
    private Proxy proxy;

}
