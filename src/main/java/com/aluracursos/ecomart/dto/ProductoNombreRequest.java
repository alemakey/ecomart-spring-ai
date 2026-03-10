package com.aluracursos.ecomart.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para endpoints que reciben el nombre de un producto.
 * Se utiliza en /api/v1/categorizar y /api/v1/generar-producto.
 */
public record ProductoNombreRequest(
        @NotBlank(message = "El nombre del producto no puede estar vacío") String nombre) {
}
