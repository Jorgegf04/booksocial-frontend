package com.example.booksocial_frontend.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class FileUploadClientService {

    @Value("${api.base-url:http://localhost:9999/api}")
    private String apiBaseUrl;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.create(Objects.requireNonNull(apiBaseUrl));
    }

    public String uploadImage(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image";

            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() { return filename; }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            return restClient.post()
                .uri("/upload")
                .contentType(Objects.requireNonNull(MediaType.MULTIPART_FORM_DATA))
                .body(body)
                .retrieve()
                .body(String.class);

        } catch (Exception e) {
            throw new RuntimeException("Error al subir la imagen: " + e.getMessage(), e);
        }
    }
}
