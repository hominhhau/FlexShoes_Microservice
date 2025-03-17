package iuh.fit.se.profileservice.service;

import iuh.fit.se.profileservice.dtos.CustomerDTO;

import java.util.List;

public interface CustomerService {

        List<CustomerDTO> getAllCustomer();
        CustomerDTO save(CustomerDTO customersDTO);
        CustomerDTO findByID(Integer id);

    }
