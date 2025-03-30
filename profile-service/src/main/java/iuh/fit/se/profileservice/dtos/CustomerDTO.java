package iuh.fit.se.profileservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDTO {
    @Min(value = 0, message = "User ID must be greater than 0")
    Long userID;

    Long profileKey;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not in valid format")
    String email;

    @NotBlank(message = "First name is required")
    String firstName;

    @NotBlank(message = "Last name is required")
    String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Phone number must start with 0 and consist of 10 or 11 characters.")
    String phoneNumber;

    @NotBlank(message = "Gender is required")
    String gender;

    Set<String> address;
}
