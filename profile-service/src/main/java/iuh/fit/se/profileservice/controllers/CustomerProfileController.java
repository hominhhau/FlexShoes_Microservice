package iuh.fit.se.profileservice.controllers;


import iuh.fit.se.profileservice.dtos.ApiResponse;
import iuh.fit.se.profileservice.dtos.CustomerDTO;
import iuh.fit.se.profileservice.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerProfileController {
    CustomerService customerService;

    @PostMapping("/add")
    public ApiResponse<CustomerDTO> register(@RequestBody @Valid CustomerDTO customerDTO) {
        ApiResponse<CustomerDTO> result = new ApiResponse<CustomerDTO>();
        result.setResponse(customerService.save(customerDTO));
        return result;

    }
    @GetMapping("/findByID/{id}")
    public CustomerDTO findByID(@PathVariable Integer id) {
        return customerService.findByID(id);
    }


    //API để lấy tất cả khách hàng (GET /api/customers)
    @GetMapping("/getAll")
    public List<CustomerDTO> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomer();
        return new ResponseEntity<>(customers, HttpStatus.OK).getBody();
    }
    @PostMapping("/update/{id}")
    public ApiResponse<CustomerDTO> update(@PathVariable Integer id, @RequestBody @Valid CustomerDTO customerDTO) {
        ApiResponse<CustomerDTO> result = new ApiResponse<CustomerDTO>();
        result.setResponse(customerService.updateByID(id, customerDTO));
        return result;
    }
}
