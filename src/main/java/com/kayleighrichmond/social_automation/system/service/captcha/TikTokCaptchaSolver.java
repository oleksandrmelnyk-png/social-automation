package com.kayleighrichmond.social_automation.system.service.captcha;

import com.kayleighrichmond.social_automation.common.exception.CaptchaException;
import com.kayleighrichmond.social_automation.system.client.playwright.PlaywrightHelper;
import com.kayleighrichmond.social_automation.system.client.sadcaptcha.SadCaptchaClient;
import com.kayleighrichmond.social_automation.system.client.sadcaptcha.dto.RotateCaptchaResponse;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Mouse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;

import static com.kayleighrichmond.social_automation.common.helper.WaitHelper.waitRandomlyInRange;
import static com.kayleighrichmond.social_automation.domain.tiktok.common.constants.TikTokSelectors.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokCaptchaSolver implements CaptchaSolver {

    private final SadCaptchaClient sadCaptchaClient;

    private final PlaywrightHelper playwrightHelper;

    @Override
    public void solve(Page page) {
        try {
            solveRotate(page);
        } catch (Exception e) {
            log.error("Captcha not solved: ", e);
            solvePuzzle(page);
        }
    }

    private void solveRotate(Page page) {
        log.info("Solving rotation captcha");

        for (int i = 1; i <= 3; i++) {
            if (hasSolved(page)) {
                log.info("Successfully solved captcha");
                break;
            }

            log.info("Attempting to solve the captcha: " + i + "/3");

            try {
                byte[] outerBytes = page.locator(CAPTCHA_IMG).first().screenshot();
                byte[] innerBytes = page.locator(CAPTCHA_IMG).nth(1).screenshot();

                String outerBase64 = Base64.getEncoder().encodeToString(outerBytes);
                String innerBase64 = Base64.getEncoder().encodeToString(innerBytes);

                RotateCaptchaResponse rotateCaptchaResponse = sadCaptchaClient.rotate(outerBase64, innerBase64);

                Locator slidebar = page.locator(CAPTCHA_SLIDEBAR);
                Locator sliderIcon = page.locator(CAPTCHA_SLIDER_ICON);

                BoundingBox slideBox = slidebar.boundingBox();
                BoundingBox iconBox = sliderIcon.boundingBox();

                double l_s = slideBox.width;
                double l_i = iconBox.width;

                double maxDistance = l_s - l_i;
                double d = (maxDistance * rotateCaptchaResponse.getAngle()) / 360.0;

                double noise = (Math.random() - 0.5) * 4;
                double targetX = iconBox.x + iconBox.width / 2 + d + noise;
                double startX = iconBox.x + iconBox.width / 2;
                double startY = iconBox.y + iconBox.height / 2;

                page.mouse().move(startX, startY);
                waitRandomlyInRange(200, 400);
                page.mouse().down();

                int steps = 10;
                for (int j = 1; j <= steps; j++) {
                    double x = startX + ((targetX - startX) * j / steps) + (Math.random() - 0.5) * 2;
                    page.mouse().move(x, startY, new Mouse.MoveOptions().setSteps(1));
                    waitRandomlyInRange(20, 50);
                }

                waitRandomlyInRange(100, 200);
                page.mouse().up();
                waitRandomlyInRange(300, 500);

            } catch (InterruptedException e) {
                throw new CaptchaException("Rotation captcha solving failed");
            }
        }
    }

    private void solvePuzzle(Page page) {
        log.info("Solving puzzle captcha");

        System.out.println(page.locator("body").innerHTML());
        throw new CaptchaException("Appeared unhandled Puzzle captcha");
    }

    private boolean hasSolved(Page page) {
        Locator captchaLocator = page.locator(CAPTCHA_IMG).first();
        playwrightHelper.waitForSelector(captchaLocator, 4000);
        return !captchaLocator.isVisible();
    }
}
