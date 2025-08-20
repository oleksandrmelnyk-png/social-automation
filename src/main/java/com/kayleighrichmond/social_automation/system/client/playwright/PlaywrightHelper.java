package com.kayleighrichmond.social_automation.system.client.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class PlaywrightHelper {

    public void waitForSelectorAndAct(Page page, String selector, Consumer<Locator> consumer) {
        Locator locator = page.locator(selector);
        boolean appeared = waitForSelector(locator);

        if (appeared) {
            consumer.accept(locator);
        }
    }

    public boolean waitForSelector(Locator locator) {
        return waitForSelector(locator, 7000);
    }

    public boolean waitForSelector(Locator locator, int duration) {
        boolean appeared;

        try {
            locator.waitFor(new Locator.WaitForOptions().setTimeout(duration));
            appeared = locator.isVisible();
        } catch (PlaywrightException e) {
            appeared = false;
        }

        return appeared;
    }

}
