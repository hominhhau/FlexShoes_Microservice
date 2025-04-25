package com.microservice.api_gateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.api_gateway.dto.ApiResponse;
import com.microservice.api_gateway.service.UsersService;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
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

    @NonFinal
    String[] publicEndpoints = {
            "/users/sign-in",
            "/users/sign-up",
            "/users/refresh-token",
            "/users/introspect",
            "/invoices/recent",
            "/inventory/filterProductsByCriteria",
            "/inventory/getAllProducts",
            "/inventory/getAllProductTypes",
            "/inventory/createProduct",
            "/inventory/attachInventoryToProduct",
            "/inventory/getAllColors",
            "/inventory/getAllSizes",
            "/inventory/getAllProductTypes",
            "/inventory/getAllBrandTypes",
            "/notification/registration-success",

            "/inventory/getNumberOfProductsById/**",

    };

    @NonFinal
    @Value("${app.api-prefix}")
    private String apiPrefix;

    // This filter is executed for every request
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("AuthenticationFilter - " + exchange.getRequest().getPath());

        if (isPublicEndpoint(exchange.getRequest())) {
            log.info("Public endpoint, skipping authentication");
            return chain.filter(exchange);
        }

        // Get the token from the request
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader)) {
            log.info("Authorization header is missing");
            return unauthenticated(exchange.getResponse());
        }

        // Get the token from the header and remove the "Bearer " prefix
        String token = authHeader.get(0).substring(7);
        log.info("Token: " + token);

        log.info("Introspecting token");
        // Call the users service to introspect the token
        return usersService.introspect(token).flatMap(introspectResponse -> {
            if (introspectResponse.getResponse().isValid()) {
                log.info("Token is valid");
                return chain.filter(exchange);
            }
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

    // This method checks if the request is for a public endpoint
    private boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        log.info("Request path: " + path);
        for (String endpoint : publicEndpoints) {
            if (path.contains(apiPrefix + endpoint)) {
                return true;
            }
        }
        return false;
    }
}
