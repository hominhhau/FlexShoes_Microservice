package iuh.fit.se.profileservice.service;

import iuh.fit.se.profileservice.dtos.ApiResponse;
import iuh.fit.se.profileservice.dtos.CustomerDTO;
import iuh.fit.se.profileservice.exceptions.ProfileAlreadyExistsException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomerService {

    ResponseEntity<ApiResponse<?>>  getAllCustomer();
    ResponseEntity<ApiResponse<?>> createCustomer(CustomerDTO customersDTO) throws ProfileAlreadyExistsException;
    ResponseEntity<ApiResponse<?>> findByID(Long id);
    ResponseEntity<ApiResponse<?>> updateByID(Long id, CustomerDTO customerDTO);
    ResponseEntity<ApiResponse<?>> findByuserID(Long userID);
    boolean checkProfile(String phone);

    }
