package com.kayleighrichmond.social_automation.service.client.nst.dto.builder;

import com.kayleighrichmond.social_automation.domain.entity.Proxy;
import com.kayleighrichmond.social_automation.service.client.nst.dto.CreateProfileRequest;

import java.util.List;

public class CreateProfileRequestBuilder {

    public static CreateProfileRequest buildCreateProfileRequest(String profileName, Proxy proxy) {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setName(profileName);
        request.setPlatform("Windows");
        request.setProxy("http://%s:%s@%s:%d".formatted(
                proxy.getUsername(),
                proxy.getPassword(),
                proxy.getHost(),
                proxy.getPort()
        ));
        CreateProfileRequest.Fingerprint fingerprint = new CreateProfileRequest.Fingerprint();
        CreateProfileRequest.Fingerprint.Localization localization = new CreateProfileRequest.Fingerprint.Localization();
        localization.setLanguage("en");
        localization.setLanguages(List.of("en-US"));
        localization.setTimezone("America/New_York");
        request.setFingerprint(fingerprint);
        return request;
    }
}
