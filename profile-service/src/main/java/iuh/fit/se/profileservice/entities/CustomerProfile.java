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
public class CustomerProfile  extends  BaseEntity implements Serializable {
    Long userID; //userID : la id cua account da tao ra ben user service khi sign up

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMER_ID", columnDefinition = "int", updatable = false, insertable = false)
    Integer customerId;

    @Column(name = "CUSTOMER_NAME", columnDefinition = "nvarchar(105)")
     String customerName;

    @Column(name = "PHONE_NUMBER", columnDefinition = "nvarchar(12)")
     String phoneNumber;

    @Column(name = "GENDER")
     String gender;

    @ElementCollection
    @CollectionTable(name = "ADDRESS", joinColumns =  @JoinColumn(name = "CUSTOMER_ID"))
    @Column(name = "ADDRESS", columnDefinition = "nvarchar(105)")
     Set<String> address;
}

