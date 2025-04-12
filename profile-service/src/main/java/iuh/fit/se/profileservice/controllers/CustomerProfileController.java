package iuh.fit.se.profileservice.controllers;


import iuh.fit.se.profileservice.dtos.ApiResponse;
import iuh.fit.se.profileservice.dtos.CustomerDTO;
import iuh.fit.se.profileservice.exceptions.ProfileAlreadyExistsException;
import iuh.fit.se.profileservice.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerProfileController {
    CustomerService customerService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody @Valid CustomerDTO customerDTO) throws ProfileAlreadyExistsException {
        return customerService.createCustomer(customerDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> findByID(@PathVariable Long id) {
        return customerService.findByID(id);
    }


    @GetMapping("/user/{userID}")
    public ResponseEntity<ApiResponse<?>> findByuserID(@PathVariable Long userID) {
        return customerService.findByuserID(userID);
    }
    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<?>> getAllCustomers() {
       return customerService.getAllCustomer();
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @RequestBody @Valid CustomerDTO customerDTO) {
        return customerService.updateByID(id, customerDTO);

    }
    @PostMapping("/check-profile")
    public boolean checkProfile(@RequestBody String phoneNumber) {
        return customerService.checkProfile(phoneNumber);
    }
}
