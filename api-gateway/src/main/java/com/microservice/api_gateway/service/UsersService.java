package com.microservice.api_gateway.service;

import com.microservice.api_gateway.dto.ApiResponse;
import com.microservice.api_gateway.dto.request.TokenRequest;
import com.microservice.api_gateway.dto.response.IntrospectResponse;
import com.microservice.api_gateway.repository.UsersClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UsersService {

    UsersClient userClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token){
        return userClient.introspect(TokenRequest.builder()
                .token(token)
                .build());
    }
}
