package com.kayleighrichmond.social_automation.system.service.captcha;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.kayleighrichmond.social_automation.common.helper.WaitHelper.waitRandomlyInRange;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwoCaptchaSolver implements CaptchaSolver {

    private static final String TWO_CAPTCHA_URL = "chrome-extension://%s/options/options.html";

    private static final String TWO_CAPTCHA_API_KEY_SELECTOR = "input[name='apiKey']";

    private static final String TWO_CAPTCHA_AUTOSOLWE = "input[name^='autoSolve']";

    private static final Set<String> SKIP_CAPTCHA = Set.of(
            "autoSolveRecaptchaV2",
            "autoSolveRecaptchaV3",
            "autoSolveAmazonWaf"
    );
    private static final Set<String> TURN_OFF_CAPTCHA = Set.of(
            "enabledForRecaptchaV2",
            "enabledForRecaptchaV3",
            "enabledForAmazonWaf"
    );

    @Override
    public void solve(Page page) {

        try {
            tryNavigateExtension(page);

            ElementHandle element = findElement(page);
            if (element == null) {
                throw new RuntimeException("2captcha extension not found");
            }

            String extensionId = element.getProperty("id").toString();

            page.navigate(TWO_CAPTCHA_URL.formatted(extensionId));
            waitRandomlyInRange(1000, 1800);
            ElementHandle elementHandle = page.querySelector(TWO_CAPTCHA_API_KEY_SELECTOR);

            if (elementHandle != null && elementHandle.inputValue().equals("c7d86da3fc9bb5eece6bf9a95f29e95d")) {
                return;
            }

            if (elementHandle == null) {
                throw new RuntimeException("capsolver error");
            }

            elementHandle.fill("c7d86da3fc9bb5eece6bf9a95f29e95d");
            page.querySelector("#connect").click();

            Locator select = page.locator("select[name='buttonPosition']");
            select.selectOption(new SelectOption().setValue("fixed"));


            page.querySelector("#autoSubmitForms").click();
            for (ElementHandle handle : page.querySelectorAll(TWO_CAPTCHA_AUTOSOLWE)) {
                var id = handle.getAttribute("id");
                if (id != null && SKIP_CAPTCHA.contains(id)) {
                    continue;
                }
                handle.click(new ElementHandle.ClickOptions().setTimeout(10_000));
            }
            for (String s : TURN_OFF_CAPTCHA) {
                var checkbox = page.locator("input[id=\"%s\"][type=\"checkbox\"]".formatted(s));
                if (checkbox.count() == 0) {
                    continue;
                }
                checkbox.evaluate("""
                    el => {
                        el.checked = false;
                        el.dispatchEvent(new Event('change', { bubbles: true }));
                    }
                """);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static ElementHandle findElement(Page page) {
        int timeoutMs = 10 * 1000;

        try {
            page.waitForSelector("extensions-item",
                    new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(timeoutMs));

            var result = page.querySelectorAll("extensions-item");
            if (!result.isEmpty()) {
                log.debug("Found element: selector={}", "extensions-item");
            } else {
                log.debug("Element not found: selector={}", "extensions-item");
            }
            return result.stream()
                    .filter(it -> it.evaluateHandle("el => el.shadowRoot").asElement().textContent().contains("Captcha Solver: Auto Recognition and Bypass"))
                    .findFirst()
                    .orElseThrow();
        } catch (PlaywrightException e) {
            log.debug("Failed to find element: selector={}", "extensions-item");
            return null;
        }
    }

    private static void tryNavigateExtension(Page page) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            try {
                page.navigate("chrome://extensions/");
                return;
            } catch (Exception e) {
                waitRandomlyInRange(500, 1000);
            }
        }
    }

    public void waitSolving(Page page) throws InterruptedException {
            for (int i = 0; i < 100; i++) {
                try {
                    var item = page.locator("div[class=\"captcha-solver-info\"]", new Page.LocatorOptions().setHasText("Solving..."));
                    if (item.count() == 0) {
                        if (i == 0) return;
                        else break;
                    }
                    if (i % 10 == 9) {
                        log.info("Waiting for solving... step {}", i);
                    }
                } catch (Exception e) {
                    log.warn("Error while waiting for solving");
                    return;
                }
                waitRandomlyInRange(1_500, 2_000);
            }
    }

    public boolean hasSolved(Page page) {
        return page.locator("div[class=\"captcha-solver-info\"]", new Page.LocatorOptions().setHasText("Solve with 2Captcha")).count() > 0;
    }
}
