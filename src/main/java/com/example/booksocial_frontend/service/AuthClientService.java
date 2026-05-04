package com.example.booksocial_frontend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.JwtResponseDTO;
import com.example.booksocial_frontend.dto.LoginRequestDTO;
import com.example.booksocial_frontend.dto.RegisterRequestDTO;

import jakarta.annotation.PostConstruct;

@Service
public class AuthClientService {

    @Value("${api.base-url:http://localhost:9999/api}")
    private String apiBaseUrl;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.create(apiBaseUrl + "/auth");
    }

    public JwtResponseDTO login(LoginRequestDTO request) {
        return restClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(JwtResponseDTO.class);
    }

    public void register(RegisterRequestDTO request) {
        restClient.post()
                .uri("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
