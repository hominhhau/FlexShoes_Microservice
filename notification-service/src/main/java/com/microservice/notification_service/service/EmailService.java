package com.microservice.notification_service.service;

import com.microservice.notification_service.dto.reponse.EmailResponse;
import com.microservice.notification_service.dto.request.EmailRequest;
import com.microservice.notification_service.repository.httpclient.EmailClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    EmailClient emailClient;

    public EmailResponse sendEmail(String apiKey, EmailRequest request) {
        return emailClient.sendEmail(apiKey, request);
    }
}