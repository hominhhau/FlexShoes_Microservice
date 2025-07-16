package iuh.fit.se.userservice.repositories.httpClient;

import iuh.fit.se.userservice.configs.AuthenticationRequestInterceptor;
import iuh.fit.se.userservice.dtos.Recipient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${url.notificationService}", configuration = {AuthenticationRequestInterceptor.class})
public interface NotificationClient {

    @PostMapping(value ="/registration-success", produces = "application/json")
    void sendRegistrationEmail(@RequestBody Recipient dto);
}
