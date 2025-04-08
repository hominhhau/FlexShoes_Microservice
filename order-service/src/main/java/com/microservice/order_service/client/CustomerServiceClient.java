package com.microservice.order_service.client;

import com.microservice.order_service.dto.ApiResponse;
import com.microservice.order_service.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service", url = "${customer-service.url}")
public interface CustomerServiceClient {

    @GetMapping("/profile/{id}")
    ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable("id") Long id);


//    @GetMapping("/api/customers/name/{name}")
//    CustomerDto getCustomerByName(@PathVariable("name") String name);
}
