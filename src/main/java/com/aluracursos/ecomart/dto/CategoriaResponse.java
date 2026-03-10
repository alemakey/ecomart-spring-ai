package com.aluracursos.ecomart.dto;

/**
 * DTO de respuesta del endpoint /api/v1/categorizar.
 * Incluye el campo {@code tokensUsados} para visibilidad de costos (JTokkit).
 */
public record CategoriaResponse(
        String nombre,
        String categoria,
        int tokensUsados) {
}
