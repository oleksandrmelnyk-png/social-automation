package com.kayleighrichmond.social_automation.service.api.account.tiktok;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Month;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TikTokSelectors {

    static String HOME_SIGN_UP = "h3:has-text('Sign up')";

    static String HOME_L0G_IN = "h3:has-text('Log in')";

    static String SIGN_UP_USE_PHONE_OR_EMAIL = "text=Use phone or email";

    static String LOG_IN_USE_PHONE_OR_EMAIL_OR_USERNAME = "text=Use phone / email / username";

    static String SIGN_UP_WITH_EMAIL = "text=Sign up with email";

    static String LOG_IN_WITH_EMAIL_OR_USERNAME = "text=Log in with email or username";

    static String MONTH_DIV = "div[role='combobox'][aria-label^='Month']";

    static String DAY_DIV = "div[role='combobox'][aria-label^='Day']";

    static String YEAR_DIV = "div[role='combobox'][aria-label^='Year']";

    static String SIGN_UP_EMAIL_INPUT = "input[placeholder='Email address']";

    static String LOG_IN_EMAIL_INPUT = "input[placeholder='Email or username']";

    static String PASSWORD_INPUT = "input[placeholder='Password']";

    static String SEND_CODE_BUTTON = "button:has-text('Send code')";

    static String CODE_INPUT = "input[placeholder='Enter 6-digit code']";

    static String RESEND_CODE_TIMEOUT = "button[disabled]:has-text('Resend code:')";

    static String NEXT_BUTTON = "button:has-text('Next')";

    static String SIGN_UP_BUTTON = "button:has-text('Sign up')";

    static String LOG_IN_BUTTON = "button:has-text('Log in')";

    static String SELECT_ADD = "div.webapp-pa-prompt_container__pa_button";

    static String CAPTCHA_DIV = "#captcha-verify-container-main-page";

    static String LANGUAGE_SELECT = "select.tiktok-vm0biq-SelectFormContainer";

    static String USERNAME_INPUT = "input[name='new-username']";

    static String AVATAR_ICON = "img.css-eady97-ImgAvatar.e1dsb8x11";

    static String LIKE_BUTTON = "button[aria-label^='Like video']";

    static String NEXT_VIDEO_BUTTON = "div.css-guqx24-DivFeedNavigationContainer > div.css-1dux0b3:nth-child(2) > button.TUXButton";

    static String selectMonth(Month month) {
        return "div[role='option'] >> text=%s".formatted(month.name().charAt(0) + month.name().substring(1).toLowerCase());
    }

    static String selectDay(int dayOfMonth) {
        return "div[role='option'] >> text=%s".formatted(dayOfMonth);
    }

    static String selectYear(int year) {
        return "div[role='option'] >> text=%d".formatted(year);
    }

}
