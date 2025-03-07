package com.microservice.notification_service.dto.reponse;


import com.microservice.notification_service.dto.request.Recipient;
import com.microservice.notification_service.dto.request.Sender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailResponse {
    String messageId;
}
