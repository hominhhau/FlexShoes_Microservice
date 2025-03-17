package iuh.fit.se.profileservice.service.impl;

import iuh.fit.se.profileservice.dtos.CustomerDTO;
import iuh.fit.se.profileservice.mapper.CustomerMapper;
import iuh.fit.se.profileservice.repository.CustomerRepository;
import iuh.fit.se.profileservice.service.CustomerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomerServiceImpl implements CustomerService {
    CustomerRepository customerRepository;
    CustomerMapper customerMapper;


    @Override
    public List<CustomerDTO> getAllCustomer() {
        return customerRepository.findAll().stream()
                .map(item -> customerMapper.mapToCustomerDTO(item))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO save(CustomerDTO customersDTO) {
        // TODO Auto-generated method stub
        return customerMapper.mapToCustomerDTO(customerRepository.save(customerMapper.mapToCustomerProfile(customersDTO)));
    }

    @Override
    public CustomerDTO findByID(Integer id) {
        // TODO Auto-generated method stub
        return customerMapper.mapToCustomerDTO(customerRepository.findById(id).get());
    }
}
