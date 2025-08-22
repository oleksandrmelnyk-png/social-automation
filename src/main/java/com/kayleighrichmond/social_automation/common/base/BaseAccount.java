package com.kayleighrichmond.social_automation.common.base;

import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.type.Status;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
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
public abstract class BaseAccount {

    @Column(unique = true)
    private String email;

    private String password;

    private String countryCode;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Action action;

    private int likedPosts;

    private String accountLink;

    @ManyToOne
    @JoinColumn(name = "proxy_id")
    private Proxy proxy;

    private String nstProfileId;

    private String executionMessage;
}
