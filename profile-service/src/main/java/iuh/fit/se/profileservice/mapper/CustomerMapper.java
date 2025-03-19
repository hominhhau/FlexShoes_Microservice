package iuh.fit.se.profileservice.mapper;



import iuh.fit.se.profileservice.dtos.CustomerDTO;
import iuh.fit.se.profileservice.entities.CustomerProfile;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static CustomerDTO mapToCustomerDTO(CustomerProfile customer){
        return modelMapper.map(customer, CustomerDTO.class);
    }



    public static CustomerProfile mapToCustomerProfile(CustomerDTO customerDTO){
        return modelMapper.map(customerDTO, CustomerProfile.class);
    }
}
