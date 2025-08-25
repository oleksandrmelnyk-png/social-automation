package com.kayleighrichmond.social_automation.domain.tiktok.model;

import com.kayleighrichmond.social_automation.common.BaseAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.model.embedded.Dob;
import com.kayleighrichmond.social_automation.domain.tiktok.model.embedded.Name;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TikTokAccount extends BaseAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;

    @Embedded
    private Name name;

    @Embedded
    private Dob dob;

}
