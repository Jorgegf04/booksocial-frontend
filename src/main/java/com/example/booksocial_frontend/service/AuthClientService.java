package com.example.booksocial_frontend.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.JwtResponseDTO;
import com.example.booksocial_frontend.dto.LoginRequestDTO;

@Service
public class AuthClientService {

    private final RestClient restClient = RestClient.create("http://localhost:9999");

    public JwtResponseDTO login(LoginRequestDTO request) {
        return restClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(JwtResponseDTO.class);
    }
}
