package iuh.fit.se.profileservice.service.impl;

import iuh.fit.se.profileservice.dtos.ApiResponse;
import iuh.fit.se.profileservice.dtos.CustomerDTO;
import iuh.fit.se.profileservice.entities.CustomerProfile;
import iuh.fit.se.profileservice.exceptions.ProfileAlreadyExistsException;
import iuh.fit.se.profileservice.mapper.CustomerMapper;
import iuh.fit.se.profileservice.repository.CustomerRepository;
import iuh.fit.se.profileservice.service.CustomerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>>  getAllCustomer() {
        List<CustomerDTO> customers = customerRepository.findAll().stream()
                .map(item -> customerMapper.mapToCustomerDTO(item))
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                ApiResponse.<List<CustomerDTO>>builder()
                        .status("SUCCESS")
                        .message("Get all customers successful")
                        .response(customers)
                        .build());

    }

    @Override
    public ResponseEntity<ApiResponse<?>> createCustomer(CustomerDTO customerDTO) throws ProfileAlreadyExistsException {

        try {
            CustomerProfile customerProfile = customerMapper.mapToCustomerProfile(customerDTO);
            CustomerDTO result = customerMapper.mapToCustomerDTO(customerRepository.save(customerProfile));
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.<CustomerDTO>builder()
                            .status("SUCCESS")
                            .message("Get all customers successful")
                            .response(result)
                            .build());

        } catch (Exception e) {
            throw new ProfileAlreadyExistsException("Customer Profile already exists");
        }

    }

    @Override
    public ResponseEntity<ApiResponse<?>> findByID(Long id) {
        // TODO Auto-generated method stub
        CustomerDTO customerDTO =  customerMapper.mapToCustomerDTO(customerRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Customer not found with id : " + id)
        ));
        return ResponseEntity.ok(
                ApiResponse.<CustomerDTO>builder()
                        .status("SUCCESS")
                        .message("Found customer with id " + id + " successful")
                        .response(customerDTO)
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateByID(Long id, CustomerDTO customerDTO) {
        CustomerProfile customerProfile = customerRepository.findById(id).orElseThrow(
                ()->  new RuntimeException("Customer with id " + id + " is not found!")
        );
        CustomerProfile newCustomer = customerMapper.mapToCustomerProfile(customerDTO);
        try {
            newCustomer.setProfileKey(customerProfile.getProfileKey());
            CustomerDTO result = customerMapper.mapToCustomerDTO(customerRepository.save(newCustomer));
            return ResponseEntity.ok(
                    ApiResponse.<CustomerDTO>builder()
                            .status("SUCCESS")
                            .message("Update customer with id " + id + " successful")
                            .response(result)
                            .build());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
