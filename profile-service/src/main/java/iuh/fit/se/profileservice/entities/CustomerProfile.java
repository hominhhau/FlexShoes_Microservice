package iuh.fit.se.profileservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Table(name = "CustomerProfile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode( of = "customerId")
public class CustomerProfile  extends  BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROFILE_KEY", updatable = false, insertable = false)
    Long profileKey;

    @Column(name = "USER_ID", columnDefinition = "bigint", unique = true, nullable = false)
    Long userID; //userID : la id cua account da tao ra ben user service khi sign up

    @Column(name = "FIRST_NAME", columnDefinition = "nvarchar(50)")
     String firstName;

    @Column(name = "LAST_NAME", columnDefinition = "nvarchar(50)")
    String lastName;

    @Column(name = "PHONE_NUMBER", columnDefinition = "nvarchar(12)", unique = true)
     String phoneNumber;

    @Column(name = "GENDER")
     String gender;

    @ElementCollection
    @CollectionTable(name = "ADDRESS", joinColumns =  @JoinColumn(name = "CUSTOMER_ID"))
    @Column(name = "ADDRESS", columnDefinition = "nvarchar(105)")
     Set<String> address;
}

