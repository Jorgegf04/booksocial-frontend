package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.WorkResponseDTO;

import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de consumir la API REST de obras (Work)
 * del backend BookSocial.
 *
 * <p>
 * Utiliza {@link RestClient} para realizar peticiones HTTP
 * al backend y obtener los datos del catálogo.
 * </p>
 *
 * <p>
 * Responsabilidades:
 * </p>
 * <ul>
 * <li>Obtener listado de obras</li>
 * <li>Aplicar filtros</li>
 * <li>Consumir endpoints REST</li>
 * </ul>
 *
 * @author Jorge
 * @since 2026
 */
@Service
@RequiredArgsConstructor
public class WorkClientService {

  private final RestClient restClient = RestClient.create("http://localhost:9999/api/works");

  /**
   * Obtiene todas las obras del catálogo.
   *
   * @return lista de obras
   */
  public List<WorkResponseDTO> getAllWorks() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<WorkResponseDTO>>() {
        });
  }

  /**
   * Obtiene una obra por su ID.
   *
   * @param id identificador de la obra
   * @return obra encontrada
   */
  public WorkResponseDTO getWorkById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(WorkResponseDTO.class);
  }

  /**
   * Realiza una búsqueda simple de obras.
   *
   * @param title  título parcial
   * @param genre  género
   * @param rating rating mínimo
   * @return lista filtrada de obras
   */
  public List<WorkResponseDTO> search(String title, String genre, Double rating) {

    return restClient.get()
        .uri(uriBuilder -> {
          var builder = uriBuilder.path("/search");
          // Solo añadir el parámetro si tiene valor; de lo contrario UriComponentsBuilder
          // lo serializa como la cadena "null" y el backend no encuentra nada.
          if (title != null && !title.isBlank()) builder = builder.queryParam("title", title);
          if (genre != null) builder = builder.queryParam("genre", genre);
          if (rating != null && rating > 0) builder = builder.queryParam("rating", rating);
          return builder.build();
        })
        .retrieve()
        .body(new ParameterizedTypeReference<List<WorkResponseDTO>>() {
        });
  }

}