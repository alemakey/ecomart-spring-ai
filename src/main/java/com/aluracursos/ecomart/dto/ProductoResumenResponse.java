package com.aluracursos.ecomart.dto;

/**
 * DTO de respuesta del endpoint /api/v1/resumir.
 * Incluye el campo {@code tokensUsados} para visibilidad de costos (JTokkit).
 */
public record ProductoResumenResponse(
        String nombre,
        String resumen,
        int tokensUsados) {
}
