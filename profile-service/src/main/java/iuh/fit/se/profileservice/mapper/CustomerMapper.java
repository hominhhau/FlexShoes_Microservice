package iuh.fit.se.profileservice.mapper;

import iuh.fit.se.profileservice.dtos.CustomerDTO;
import iuh.fit.se.profileservice.entities.CustomerProfile;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CustomerMapper {
    public CustomerDTO mapToCustomerDTO(CustomerProfile customer);
    public CustomerProfile mapToCustomerProfile(CustomerDTO customerDTO);
}
