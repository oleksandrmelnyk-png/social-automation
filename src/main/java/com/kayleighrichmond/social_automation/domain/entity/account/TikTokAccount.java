package com.kayleighrichmond.social_automation.domain.entity.account;

import com.kayleighrichmond.social_automation.domain.entity.account.embedded.Dob;
import com.kayleighrichmond.social_automation.domain.entity.account.embedded.Name;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class TikTokAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Embedded
    private Name name;

    private String username;

    @Embedded
    private Dob dob;

}
