package com.kayleighrichmond.social_automation.service.account.tiktok.builder;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.service.mailtm.MailTmService;
import com.kayleighrichmond.social_automation.service.randomuser.RandomUserClient;
import com.kayleighrichmond.social_automation.service.randomuser.dto.RandomUserResponse;
import com.kayleighrichmond.social_automation.type.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TikTokAccountBuilder {

    private final RandomUserClient randomUserClient;

    private final MailTmService mailTmService;

    public TikTokAccount buildWithProxy(Proxy proxy) {
        RandomUserResponse.RandomResult randomUser = randomUserClient.getRandomUser();
        String password = "Qwerty1234@";
        String address = mailTmService.createAddressWithDomainOncePerSecond(randomUser.getEmail(), password);

        return TikTokAccount.builder()
                .email(address)
                .password(password)
                .name(randomUser.getName())
                .proxy(proxy)
                .status(Status.IN_PROGRESS)
                .dob(randomUser.getDob())
                .build();
    }

}
