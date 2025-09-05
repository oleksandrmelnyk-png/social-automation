package com.kayleighrichmond.social_automation.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MainSelectors {

    public static String GOOGLE_CAPTCHA = "text=Our systems have detected unusual traffic";

    public static String SELECT_GOOGLE_LANGUAGE = "button svg[aria-hidden='true'][viewBox='0 0 30 30']";

    public static String GOOGLE_ENGLISH = "li[data-hl='en-US']";

    public static String REJECT_ALL = "button:has-text('Reject all')";
}
