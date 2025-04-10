package com.microservice.api_gateway.configuration;

import com.microservice.api_gateway.repository.UsersClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

@Configuration
public class WebClientConfiguration {
    // Tạo bean cho WebClient
    @Bean
    WebClient webClient(){
        return WebClient.builder()
                .baseUrl("http://localhost:8080")  // Cấu hình baseUrl
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)  // Cấu hình mặc định header
                .build();
    }

    // Cấu hình CORS cho WebClient
    @Bean
    CorsWebFilter corsWebFilter(){
        // Cấu hình CORS
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);  // Cho phép gửi cookie
        config.setAllowedOriginPatterns(List.of("*"));  // Cho phép tất cả các nguồn gốc
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // Cho phép các phương thức HTTP
        config.setAllowedHeaders(List.of("*"));  // Cho phép tất cả các header


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // Áp dụng cấu hình CORS cho tất cả các đường dẫn

        return new CorsWebFilter(source);  // Trả về CorsWebFilter
    }

    // Tạo bean cho UsersClient
    @Bean
    UsersClient usersClient(WebClient webClient){
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();

        return httpServiceProxyFactory.createClient(UsersClient.class);
    }
}

