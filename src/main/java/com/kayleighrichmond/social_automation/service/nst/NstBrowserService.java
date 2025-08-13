package com.kayleighrichmond.social_automation.service.nst;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NstBrowserService {

    public Page getContextPage(Browser browser) {
        BrowserContext context = browser.contexts().get(0);
        List<Page> pages = context.pages();
        Page page;
        if (!pages.isEmpty()) {
            page = pages.get(0);
        } else {
            page = context.newPage();
        }

        return page;
    }

}
