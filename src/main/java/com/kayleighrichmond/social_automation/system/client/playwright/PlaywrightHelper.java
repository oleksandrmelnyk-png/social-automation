package com.kayleighrichmond.social_automation.system.client.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class PlaywrightHelper {

    public void waitForSelectorAndAct(Page page, String selector, Consumer<Locator> consumer) {
        waitForSelectorAndAct(4000, page, selector, consumer);
    }

    public void waitForSelectorAndAct(int duration, Page page, String selector, Consumer<Locator> consumer) {
        Locator locator = page.locator(selector);
        boolean appeared = waitForSelector(locator, duration);

        if (appeared) {
            consumer.accept(locator);
        }
    }

    public boolean waitForSelector(Locator locator) {
        return waitForSelector(locator, 4000);
    }

    public boolean waitForSelector(Locator locator, int duration) {
        boolean appeared;

        try {
            locator.waitFor(new Locator.WaitForOptions().setTimeout(duration));
            appeared = locator.isVisible(new Locator.IsVisibleOptions().setTimeout(duration));
        } catch (PlaywrightException e) {
            appeared = false;
        }

        return appeared;
    }

}
