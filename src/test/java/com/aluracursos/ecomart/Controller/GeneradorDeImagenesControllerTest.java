package com.aluracursos.ecomart.Controller;

import com.aluracursos.ecomart.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para GeneradorDeImagenesController.
 *
 * Este controller usa ImageModel por inyección directa de constructor.
 * La cadena mockeada es:
 *   imageModel.call(ImagePrompt) → ImageResponse → ImageGeneration → Image → url
 *
 * El ImageModel se expone como @Bean para poder stubearlo por test,
 * y se resetea en @BeforeEach para evitar contaminación entre tests.
 *
 * Nota sobre "sin body" → 503:
 *   Igual que en los otros controllers, body ausente → HttpMessageNotReadableException
 *   → handler genérico de GlobalExceptionHandler → 503.
 */
@WebMvcTest(controllers = GeneradorDeImagenesController.class)
@Import({GlobalExceptionHandler.class, GeneradorDeImagenesControllerTest.MockAiConfig.class})
class GeneradorDeImagenesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ImageModel imageModel;

    @BeforeEach
    void resetMocks() {
        reset(imageModel);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Configuración del contexto de test
    // ─────────────────────────────────────────────────────────────────────────

    static class MockAiConfig {

        @Bean
        ImageModel imageModel() {
            return mock(ImageModel.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Caso exitoso
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/imagen → 200 OK con la URL de la imagen generada")
    void generarImagenExitoso() throws Exception {
        ImageResponse imageResponse     = mock(ImageResponse.class);
        ImageGeneration imageGeneration = mock(ImageGeneration.class);
        Image imagen                    = mock(Image.class);

        when(imageModel.call(any(ImagePrompt.class))).thenReturn(imageResponse);
        when(imageResponse.getResult()).thenReturn(imageGeneration);
        when(imageGeneration.getOutput()).thenReturn(imagen);
        when(imagen.getUrl()).thenReturn("https://dalle.openai.com/imagen-de-prueba.png");

        mockMvc.perform(post("/api/v1/imagen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prompt\":\"Bolsa de tela ecológica, fondo blanco\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://dalle.openai.com/imagen-de-prueba.png"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Input inválido → 400
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/imagen con prompt vacío → 400 con mensaje de validación")
    void generarImagenPromptVacioDevuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/imagen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prompt\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.prompt").value("El prompt no puede estar vacío"));
    }

    @Test
    @DisplayName("POST /api/v1/imagen con prompt en blanco (solo espacios) → 400")
    void generarImagenPromptBlankDevuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/imagen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prompt\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.prompt").value("El prompt no puede estar vacío"));
    }

    @Test
    @DisplayName("POST /api/v1/imagen sin body → 503 (HttpMessageNotReadableException cae en handler genérico)")
    void generarImagenSinBodyDevuelve503() throws Exception {
        mockMvc.perform(post("/api/v1/imagen")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Excepción del servicio → GlobalExceptionHandler → 503
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/imagen cuando DALL-E falla → 503 con campo 'error'")
    void generarImagenExcepcionDevuelve503() throws Exception {
        when(imageModel.call(any(ImagePrompt.class)))
                .thenThrow(new RuntimeException("DALL-E 3 no disponible"));

        mockMvc.perform(post("/api/v1/imagen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prompt\":\"Bolsa de tela ecológica, fondo blanco\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("DALL-E 3 no disponible"));
    }
}
