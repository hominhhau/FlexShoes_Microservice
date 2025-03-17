package iuh.fit.se.profileservice.dtos;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDTO {
//    @NotBlank(message = "userID is required!")
    Long userID; //userID : la id cua account da tao ra ben user service khi sign up

//    @NotBlank(message = "customerId is required!")
    Integer customerId;

    @NotBlank(message = "Customer name is required!")

    String customerName;

    @NotBlank(message = "Phone number is required!")
    @Pattern(regexp = "^0\\d{9,10}$", message = "phone number start with 0 and consist of 10 or 11 characters.")
    String phoneNumber;

    @NotBlank(message = "Gender is required!")
    String gender;

    Set<String> address;

}
