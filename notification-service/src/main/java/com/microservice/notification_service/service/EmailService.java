package com.microservice.notification_service.service;

import com.microservice.notification_service.dto.reponse.EmailResponse;
import com.microservice.notification_service.dto.request.EmailRequest;
import com.microservice.notification_service.dto.request.SendEmailRequest;
import com.microservice.notification_service.dto.request.Sender;
import com.microservice.notification_service.repository.httpclient.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Service;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    EmailClient emailClient;

    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("Flex Shoes")
                        .email("chautinh05122@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();
        try {
            Dotenv dotenv = Dotenv.load();
            String apiKey = dotenv.get("SENDINBLUE_API_KEY");
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e){
            throw new ApplicationContextException("Failed to send email", e);
        }
    }
}