package com.aluracursos.ecomart.dto;

/**
 * DTO de respuesta del endpoint /api/v1/generar-producto.
 */
public record ProductoDescripcionResponse(
        String nombre,
        String descripcion) {
}
