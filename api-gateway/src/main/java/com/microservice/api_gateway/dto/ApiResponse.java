package com.microservice.api_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ApiResponse<T> {
    @Builder.Default
    private int code = 1000;
    private String status;
    private String message;
    private T response;
}
