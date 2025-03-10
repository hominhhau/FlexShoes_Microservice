package com.microservice.order_service.mapper;

import com.microservice.order_service.dto.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);
//
//    CustomerDto mapToCustomerDTO(Customer customer);
//
//    Customer mapToCustomer(CustomerDTO customerDTO);
}
