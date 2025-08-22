package com.kayleighrichmond.social_automation.config;

import com.kayleighrichmond.social_automation.system.client.nst.exception.NstBrowserException;
import com.kayleighrichmond.social_automation.system.client.playwright.dto.PlaywrightDto;
import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Boolean.TRUE;

@Slf4j
@Service
public class PlaywrightInitializer {

    private final static String NST_BROWSER_URL_TEMPLATE = "ws://%s:%s/devtool/launch/%s?x-api-key=%s&config=%%7B%%22headless%%22%%3A%s%%2C%%22autoClose%%22%%3A%s%%7D";

    @Value("${nst-browser.api-key}")
    private String NST_BROWSER_API_KEY;

    @Value("${nst-browser.headless}")
    private String NST_HEADLESS;

    @Value("${nst-browser.port}")
    private String NST_PORT;

    @Value("${nst-browser.host}")
    private String NST_HOST;

    public PlaywrightDto initPlaywright(String browserProfileId) {
        try {
            String url = String.format(
                    NST_BROWSER_URL_TEMPLATE,
                    NST_HOST,
                    NST_PORT, browserProfileId,
                    NST_BROWSER_API_KEY,
                    NST_HEADLESS,
                    TRUE
            );

            log.info("Init playwright url: {}", url);

            return initPlaywrightWithCdp(url);
        } catch (PlaywrightException e) {
            log.error("Error connecting to Nst Browser: {}", e.getMessage());
            throw new NstBrowserException("Error connecting to Nst Browser. Possibly daily quote reached");
        }
    }

    public PlaywrightDto initPlaywrightWithCdp(String cdpUrl) throws PlaywrightException {
        Playwright playwright = Playwright.create();

        Browser browser = playwright.chromium().connectOverCDP(cdpUrl);
        BrowserContext context = browser.contexts().get(0);
        Page page = context.pages().get(0);

        return new PlaywrightDto(List.of(page, context, browser, playwright), page, context);
    }
}
