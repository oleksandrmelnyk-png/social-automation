package com.kayleighrichmond.social_automation.service.playwright;

import com.kayleighrichmond.social_automation.service.playwright.dto.PlaywrightDto;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Boolean.TRUE;

@Slf4j
@Service
public class PlaywrightService {

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
        Playwright playwright = Playwright.create();

        String url = String.format(
                NST_BROWSER_URL_TEMPLATE,
                NST_HOST,
                NST_PORT, browserProfileId,
                NST_BROWSER_API_KEY,
                NST_HEADLESS,
                TRUE
        );

        log.info("Init playwright url: {}", url);

        Browser browser = playwright.chromium().connectOverCDP(url);
        BrowserContext context = browser.contexts().get(0);
        Page page = context.pages().get(0);

        return new PlaywrightDto(List.of(page, context, browser, playwright), page, context);
    }

    public void close(AutoCloseable autoCloseable) {
        try {
            autoCloseable.close();
            Thread.sleep(5);
        } catch (Exception e) {
            log.error("Can't close. {}", e.getMessage());
        }
    }
}
