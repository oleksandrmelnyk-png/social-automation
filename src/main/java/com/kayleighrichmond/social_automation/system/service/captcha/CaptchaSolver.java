package com.kayleighrichmond.social_automation.system.service.captcha;

import com.microsoft.playwright.Page;

public interface CaptchaSolver {

    void solve(Page page);

}
