package com.aluracursos.ecomart.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para el endpoint de generación de imágenes /api/v1/imagen.
 */
public record ImagenRequest(
        @NotBlank(message = "El prompt no puede estar vacío") String prompt) {
}
