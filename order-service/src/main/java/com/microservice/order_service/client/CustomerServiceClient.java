package com.microservice.order_service.client;

import com.microservice.order_service.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service", url = "${customer-service.url}")
public interface CustomerServiceClient {

    @GetMapping("/api/customers/{id}")
    CustomerDto getCustomerById(@PathVariable("id") Integer id);

//    @GetMapping("/api/customers/name/{name}")
//    CustomerDto getCustomerByName(@PathVariable("name") String name);
}
