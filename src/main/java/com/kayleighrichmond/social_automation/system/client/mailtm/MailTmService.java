package com.kayleighrichmond.social_automation.system.client.mailtm;

import com.kayleighrichmond.social_automation.system.client.mailtm.dto.GetDomainsResponse;
import com.kayleighrichmond.social_automation.system.client.mailtm.dto.GetMessagesResponse;
import com.kayleighrichmond.social_automation.system.client.mailtm.dto.GetTokenResponse;
import com.kayleighrichmond.social_automation.system.client.mailtm.exception.MailTmApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailTmService {

    private final MailTmClient mailTmClient;

    public String getCodeFromGeneratedEmail(String address, String password) {
        GetTokenResponse token = mailTmClient.getToken(address, password);
        GetMessagesResponse getMessagesResponse = waitForMessages(token.getToken());

        return retrieveCodeFromMessage(getMessagesResponse);
    }

    public String createAddressWithDomainOncePerSecond(String address, String password) {
        GetDomainsResponse domains = mailTmClient.getDomains();
        GetDomainsResponse.Domain domain = domains.getDomains().get(0);

        Random random = new Random();
        String formattedAddress = address.substring(0, address.indexOf("@")).replaceAll("\\.", "");
        String mailTmAddress = "%s%d@%s".formatted(formattedAddress, random.nextInt(9999) + 1, domain.getDomain());

        mailTmClient.createAccount(mailTmAddress, password);
        return mailTmAddress;
    }

    public String retrieveCodeFromMessage(GetMessagesResponse getMessagesResponse) {
        GetMessagesResponse.Message message = getMessagesResponse.getMessages().get(0);

        String subject = message.getSubject();
        if (subject.matches("^\\d{6}\\s.*")) {
            String code = subject.substring(0, 6);
            log.info("Successfully retrieved code from message: {}", code);
            return code;
        }

        throw new IllegalArgumentException("No code in message");
    }

    public GetMessagesResponse waitForMessages(String token) {
        int attempts = 0;

        for (int i = 0; i < 10; i++) {
            GetMessagesResponse messages = mailTmClient.getMessages(token);
            if (messages.getTotalItems() == 0) {
                try {
                    attempts++;
                    Thread.sleep(3 * 1500);

                    log.info("Waiting for email message... " + attempts + "/" + 10);

                    continue;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
            return messages;
        }

        throw new MailTmApiException("No messages received for token: " + token);
    }

}
