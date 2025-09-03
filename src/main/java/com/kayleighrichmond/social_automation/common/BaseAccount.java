package com.kayleighrichmond.social_automation.common;

import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.type.Status;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class BaseAccount extends AuditingEntity {

    @Column(unique = true)
    private String email;

    private String password;

    private String countryCode;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Action action;

    private int likedPosts;

    private int commentedPosts;

    private int publishedPosts;

    private String accountLink;

    @ManyToOne
    @JoinColumn(name = "proxy_id")
    private Proxy proxy;

    private String nstProfileId;

    private String executionMessage;
}
