package com.kayleighrichmond.social_automation.service.account.tiktok.builder;

import com.kayleighrichmond.social_automation.model.Proxy;
import com.kayleighrichmond.social_automation.model.TikTokAccount;
import com.kayleighrichmond.social_automation.service.mailtm.MailTmService;
import com.kayleighrichmond.social_automation.service.randomuser.RandomUserClient;
import com.kayleighrichmond.social_automation.service.randomuser.dto.RandomUserResponse;
import com.kayleighrichmond.social_automation.type.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.kayleighrichmond.social_automation.service.account.tiktok.TikTokConstants.TIK_TOK_BASE_URL;

@Component
@RequiredArgsConstructor
public class TikTokAccountBuilder {

    private final RandomUserClient randomUserClient;

    private final MailTmService mailTmService;

    public TikTokAccount buildWithProxy(Proxy proxy) {
        RandomUserResponse.RandomResult randomUser = randomUserClient.getRandomUser();
        String password = "Qwerty1234@";
        String address = mailTmService.createAddressWithDomainOncePerSecond(randomUser.getEmail(), password);
        String uniqueUsername = generateUniqueUsername(randomUser.getLogin().getUsername());

        return TikTokAccount.builder()
                .email(address)
                .password(password)
                .name(randomUser.getName())
                .proxy(proxy)
                .status(Status.IN_PROGRESS)
                .countryCode(proxy.getCountryCode())
                .dob(randomUser.getDob())
                .username(uniqueUsername)
                .accountLink(TIK_TOK_BASE_URL + "@" + randomUser.getLogin().getUsername())
                .build();
    }

    public String generateUniqueUsername(String basicUsername) {
        String uuid = UUID.randomUUID().toString();
        String uniqueValue = uuid.substring(0, uuid.indexOf('-'));

        return  basicUsername + uniqueValue;
    }
}
