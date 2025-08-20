package com.kayleighrichmond.social_automation.common.dto;

import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.common.type.Status;
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

    @Enumerated(EnumType.STRING)
    private Action action;

    private String executionMessage;

    private String accountLink;

    private String countryCode;

    @ManyToOne
    @JoinColumn(name = "proxy_id")
    private Proxy proxy;

    private String nstProfileId;

}
