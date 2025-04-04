package com.microservice.api_gateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.api_gateway.dto.ApiResponse;
import com.microservice.api_gateway.service.UsersService;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.net.http.HttpResponse;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {

    UsersService usersService;
    ObjectMapper objectMapper;


    // This filter is executed for every request
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("AuthenticationFilter - " + exchange.getRequest().getPath());
        // Get the token from the request
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader)) {
            log.info("Authorization header is missing");
            return unauthenticated(exchange.getResponse());
        }

        // Get the token from the header and remove the "Bearer " prefix
        String token = authHeader.get(0).substring(7);
        log.info("Token: " + token);

//        usersService.introspect(token).subscribe( response -> {
//            log.info("Response: " + response.getResponse().isValid());
//        });
//
//        // Check if the token is valid
//
//        // If the token is valid, continue the request
//        return chain.filter(exchange);

        return usersService.introspect(token).flatMap(introspectResponse -> {
            if (introspectResponse.getResponse().isValid())

                return chain.filter(exchange);
            else {
                log.info("Token is invalid");

                return unauthenticated(exchange.getResponse());
            }
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    // This method is called when the token is invalid
    @Override
    public int getOrder() {
        return -1;
    }

    // This method is called when the token is invalid
    Mono<Void> unauthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .status("ERROR")
                .message("Unauthenticated")
                .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
