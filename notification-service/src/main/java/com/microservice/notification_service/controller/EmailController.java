package com.microservice.notification_service.controller;

import com.microservice.notification_service.dto.ApiResponse;
import com.microservice.notification_service.dto.reponse.EmailResponse;
import com.microservice.notification_service.dto.request.Recipient;
import com.microservice.notification_service.dto.request.SendEmailRequest;
import com.microservice.notification_service.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;

    @PostMapping("/email/send")
    ApiResponse<EmailResponse> sendEmail(@RequestBody SendEmailRequest request){
        return ApiResponse.<EmailResponse>builder()
                .result(emailService.sendEmail(request))
                .build();
    }

    @PostMapping("/registration-success")
    public ResponseEntity<?> sendRegistrationEmail(@RequestBody Recipient dto) {
        emailService.sendRegistrationSuccessEmail(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order-success")
    public ResponseEntity<?> sendOrderEmail(@RequestBody Recipient dto) {
        emailService.sendOrderSuccessEmail(dto);
        return ResponseEntity.ok().build();
    }

}
