package iuh.fit.se.userservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenRequest {
    @NotBlank(message = "Token is not blank")
    String token;
}
