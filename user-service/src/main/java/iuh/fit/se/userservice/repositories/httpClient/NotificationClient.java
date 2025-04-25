package iuh.fit.se.userservice.repositories.httpClient;

import iuh.fit.se.userservice.dtos.Recipient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", path = "/notification")
public interface NotificationClient {

    @PostMapping("/registration-success")
    void sendRegistrationEmail(@RequestBody Recipient dto);
}
