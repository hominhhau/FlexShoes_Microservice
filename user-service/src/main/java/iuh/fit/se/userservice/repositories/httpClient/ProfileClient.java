package iuh.fit.se.userservice.repositories.httpClient;

import iuh.fit.se.userservice.dtos.ProfileCreationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "profile-service", url = "${url.profileService}")
public interface ProfileClient {
    @PostMapping(value = "/", produces = "application/json")
    Object createProfile(ProfileCreationRequest profile);
}
