package com.kayleighrichmond.social_automation.system.client.playwright.dto;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaywrightDto {

    private List<AutoCloseable> autoCloseables;

    private Page page;

    private BrowserContext browserContext;

}
