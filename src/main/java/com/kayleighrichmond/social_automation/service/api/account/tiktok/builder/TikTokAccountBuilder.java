package com.kayleighrichmond.social_automation.service.api.account.tiktok.builder;

import com.kayleighrichmond.social_automation.config.AppProps;
import com.kayleighrichmond.social_automation.domain.entity.Proxy;
import com.kayleighrichmond.social_automation.domain.entity.account.TikTokAccount;
import com.kayleighrichmond.social_automation.service.client.mailtm.MailTmService;
import com.kayleighrichmond.social_automation.service.client.randomuser.RandomUserClient;
import com.kayleighrichmond.social_automation.service.client.randomuser.dto.RandomUserResponse;
import com.kayleighrichmond.social_automation.domain.type.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TikTokAccountBuilder {

    private final RandomUserClient randomUserClient;

    private final MailTmService mailTmService;

    private final AppProps appProps;

    private static final String TIK_TOK_BASE_URL = "https://www.tiktok.com/";

    public TikTokAccount buildWithProxy(Proxy proxy) {
        RandomUserResponse.RandomResult randomUser = randomUserClient.getRandomUser();
        String address = mailTmService.createAddressWithDomainOncePerSecond(randomUser.getEmail(), appProps.getAccountsPassword());
        String uniqueUsername = generateUniqueUsername(randomUser.getLogin().getUsername());

        return TikTokAccount.builder()
                .email(address)
                .password(appProps.getAccountsPassword())
                .name(randomUser.getName())
                .proxy(proxy)
                .status(Status.IN_PROGRESS)
                .countryCode(proxy.getCountryCode())
                .dob(randomUser.getDob())
                .username(uniqueUsername)
                .accountLink(TIK_TOK_BASE_URL + "@" + uniqueUsername)
                .build();
    }

    public String generateUniqueUsername(String basicUsername) {
        String uuid = UUID.randomUUID().toString();
        String uniqueValue = uuid.substring(0, uuid.indexOf('-') / 2);

        return  basicUsername + uniqueValue;
    }
}
