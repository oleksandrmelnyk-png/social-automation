package com.kayleighrichmond.social_automation.domain.tiktok.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Month;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TikTokSelectors {

    public static String HOME_SIGN_UP = "a[href='https://www.tiktok.com/signup'] > h3";

    public static String HOME_L0G_IN = "h3:has-text('Log in')";

    public static String SIGN_UP_USE_PHONE_OR_EMAIL = "div[data-e2e='channel-item'] >> text='Use phone or email'";

    public static String LOG_IN_USE_PHONE_OR_EMAIL_OR_USERNAME = "text=Use phone / email / username";

    public static String SIGN_UP_WITH_EMAIL = "text=Sign up with email";

    public static String LOG_IN_WITH_EMAIL_OR_USERNAME = "text=Log in with email or username";

    public static String MONTH_DIV = "div[role='combobox'][aria-label^='Month']";

    public static String DAY_DIV = "div[role='combobox'][aria-label^='Day']";

    public static String YEAR_DIV = "div[role='combobox'][aria-label^='Year']";

    public static String SIGN_UP_EMAIL_INPUT = "input[placeholder='Email address']";

    public static String LOG_IN_EMAIL_INPUT = "input[placeholder='Email or username']";

    public static String PASSWORD_INPUT = "input[placeholder='Password']";

    public static String SEND_CODE_BUTTON = "button:has-text('Send code')";

    public static String CODE_INPUT = "input[placeholder='Enter 6-digit code']";

    public static String RESEND_CODE_TIMEOUT = "button[disabled]:has-text('Resend code:')";

    public static String NEXT_BUTTON = "button:has-text('Next')";

    public static String SIGN_UP_BUTTON = "button:has-text('Sign up')";

    public static String LOG_IN_BUTTON = "button:has-text('Log in')";

    public static String SELECT_ADD = "div.webapp-pa-prompt_container__pa_button";

    public static String CAPTCHA = "#captcha-verify-container-main-page";

    public static String LANGUAGE_SELECT = "select.tiktok-vm0biq-SelectFormContainer";

    public static String USERNAME_INPUT = "input[name='new-username']";

    public static String AVATAR_ICON = "button[aria-haspopup='dialog'] img";

    public static String NEXT_VIDEO_BUTTON = "button svg path[d^='m24 27.76']";

    public static String COMMENT_TEXT_DIV = "div[data-e2e='comment-text'] div[contenteditable='true']";

    public static String POST_COMMENT_DIV = "div[data-e2e='comment-post']";

    public static String UPLOAD_DIV = "div.TUXButton-content:has-text('Upload')";

    public static String UPLOAD_VIDEO_INPUT = "input[type='file']";

    public static String TURN_OR_OPTIONS = "button:has(div:has-text('Turn on'))";

    public static String UPLOADED_SPAN = "text=Uploaded";

    public static String POST_BUTTON = "button[data-e2e='post_video_button']";

    public static String VIDEO_PUBLISHED_SPAN = "text=Video published";

    public static String POST_NOW_BUTTON = "button:has-text('Post now')";

    public static String selectCommentButton(int videoIndex) {
        return "article[data-scroll-index='%d'] button[aria-label^='Read or add comments']".formatted(videoIndex);
    }

    public static String selectLikeButton(int videoIndex) {
        return "article[data-scroll-index='%d'] button[aria-label^='Like video']".formatted(videoIndex);
    }

    public static String selectLiveNow(int videoIndex) {
        return "article[data-scroll-index='%d']:has-text('LIVE now') button[aria-label^='Like video']".formatted(videoIndex);
    }

    public static String selectMonth(Month month) {
        return "div[role='option'] >> text=%s".formatted(month.name().charAt(0) + month.name().substring(1).toLowerCase());
    }

    public static String selectDay(int dayOfMonth) {
        return "div[role='option'] >> text=%s".formatted(dayOfMonth);
    }

    public static String selectYear(int year) {
        return "div[role='option'] >> text=%d".formatted(year);
    }

}
