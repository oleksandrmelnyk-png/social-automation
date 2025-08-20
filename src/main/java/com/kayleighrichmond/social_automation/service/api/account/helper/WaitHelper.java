package com.kayleighrichmond.social_automation.service.api.account.helper;

public class WaitHelper {

    public static void waitRandomlyInRange(long from, long to) throws InterruptedException {
        Thread.sleep((long) (from + Math.random() * (to - from)));
    }

}
