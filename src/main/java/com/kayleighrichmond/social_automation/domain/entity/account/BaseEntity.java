package com.kayleighrichmond.social_automation.domain.entity.account;

import com.kayleighrichmond.social_automation.domain.entity.Proxy;
import com.kayleighrichmond.social_automation.domain.type.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String executionMessage;

    private String accountLink;

    private String countryCode;

    @ManyToOne
    @JoinColumn(name = "proxy_id")
    private Proxy proxy;

}
