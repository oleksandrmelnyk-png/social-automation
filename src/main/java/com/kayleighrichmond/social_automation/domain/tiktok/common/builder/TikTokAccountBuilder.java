package com.kayleighrichmond.social_automation.domain.tiktok.common.builder;

import com.kayleighrichmond.social_automation.config.AppProps;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokBaseAccount;
import com.kayleighrichmond.social_automation.system.client.mailtm.MailTmService;
import com.kayleighrichmond.social_automation.system.client.randomuser.RandomUserClient;
import com.kayleighrichmond.social_automation.system.client.randomuser.dto.RandomUserResponse;
import com.kayleighrichmond.social_automation.common.type.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokConstants.TIKTOK_BASE_URL;

@Component
@RequiredArgsConstructor
public class TikTokAccountBuilder {

    private final RandomUserClient randomUserClient;

    private final MailTmService mailTmService;

    private final AppProps appProps;

    public TikTokBaseAccount buildWithProxy(Proxy proxy) {
        RandomUserResponse.RandomResult randomUser = randomUserClient.getRandomUser();
        String address = mailTmService.createAddressWithDomainOncePerSecond(randomUser.getEmail(), appProps.getAccountsPassword());
        String uniqueUsername = generateUniqueUsername(randomUser.getLogin().getUsername());

        return TikTokBaseAccount.builder()
                .email(address)
                .password(appProps.getAccountsPassword())
                .name(randomUser.getName())
                .proxy(proxy)
                .status(Status.IN_PROGRESS)
                .countryCode(proxy.getCountryCode())
                .dob(randomUser.getDob())
                .username(uniqueUsername)
                .accountLink(TIKTOK_BASE_URL + "@" + uniqueUsername)
                .build();
    }

    public String generateUniqueUsername(String basicUsername) {
        String uuid = UUID.randomUUID().toString();
        String uniqueValue = uuid.substring(0, uuid.indexOf('-') / 2);

        return  basicUsername + uniqueValue;
    }
}
