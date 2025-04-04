package iuh.fit.se.userservice.repositories.httpClient;

import iuh.fit.se.userservice.configs.AuthenticationRequestInterceptor;
import iuh.fit.se.userservice.dtos.ProfileCreationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "profile-service", url = "${url.profileService}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {

    @PostMapping(value = "/", produces = "application/json")
    Object createProfile(@RequestBody ProfileCreationRequest profile);
}
