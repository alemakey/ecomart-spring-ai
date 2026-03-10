package com.aluracursos.ecomart.dto;

/**
 * DTO de respuesta del endpoint /api/v1/imagen.
 * Contiene la URL pública de la imagen generada por DALL-E 3.
 */
public record ImagenResponse(
        String url) {
}
