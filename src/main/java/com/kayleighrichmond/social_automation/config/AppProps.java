package com.kayleighrichmond.social_automation.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties("app.props")
public class AppProps {

    private int accountsPerProxy;

    private String accountsPassword;

    private int processingThreads;

}
