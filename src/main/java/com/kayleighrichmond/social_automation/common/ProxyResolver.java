package com.kayleighrichmond.social_automation.common;

import com.kayleighrichmond.social_automation.common.type.Platform;

public interface ProxyResolver {

    void resolveActiveProxy(String accountId);

    Platform getPlatform();

}
