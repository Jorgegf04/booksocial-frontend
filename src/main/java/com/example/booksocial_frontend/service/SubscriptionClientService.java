package com.example.booksocial_frontend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.SubscriptionRequestDTO;
import com.example.booksocial_frontend.dto.SubscriptionResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

@Service
public class SubscriptionClientService {

    @Value("${api.base-url:http://localhost:9999/api}")
    private String apiBaseUrl;

    @Autowired
    private SessionJwtInterceptor jwtInterceptor;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
            .baseUrl(apiBaseUrl + "/subscriptions")
            .requestInterceptor(jwtInterceptor)
            .build();
    }

    public SubscriptionResponseDTO getByUserId(Long userId) {
        return restClient.get()
                .uri("/user/{userId}", userId)
                .retrieve()
                .body(SubscriptionResponseDTO.class);
    }

    public void cancel(Long userId) {
        restClient.delete()
                .uri("/user/{userId}", userId)
                .retrieve()
                .toBodilessEntity();
    }

    public SubscriptionResponseDTO activate(Long userId) {
        SubscriptionRequestDTO req = new SubscriptionRequestDTO();
        req.setUserId(userId);
        return restClient.post()
                .body(req)
                .retrieve()
                .body(SubscriptionResponseDTO.class);
    }
}
