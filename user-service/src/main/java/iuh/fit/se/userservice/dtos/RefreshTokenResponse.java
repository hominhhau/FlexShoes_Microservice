package iuh.fit.se.userservice.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@Builder
public class RefreshTokenResponse {
    private String newAccessToken;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Collection<? extends GrantedAuthority> roles;
}
