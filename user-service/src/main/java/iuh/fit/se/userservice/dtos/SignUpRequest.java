package iuh.fit.se.userservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "Username is required!")
    @Size(min= 5, message = "Username must have at least 5 characters!")
    @Size(max= 20, message = "Username can have have at most 20 characters!")
    private String userName;

    @Email(message = "Email is not in valid format!")
    @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Password is required!")
    @Size(min = 8, message = "Password must have at least 8 characters!")
    @Size(max = 20, message = "Password can have have at most 20 characters!")
    private String password;

    private Set<String> roles;

    @NotBlank(message = "First name is required!")
    String firstName;

    @NotBlank(message = "Last name is required!")
    String lastName;

    @NotBlank(message = "Phone number is required!")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Phone number must start with 0 and consist of 10 or 11 characters.")
    String phoneNumber;

    @NotBlank(message = "Gender is required!")
    String gender;

    Set<String> address;
}
