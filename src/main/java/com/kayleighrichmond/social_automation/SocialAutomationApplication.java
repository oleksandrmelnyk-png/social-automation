package com.kayleighrichmond.social_automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SocialAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialAutomationApplication.class, args);
	}

}
