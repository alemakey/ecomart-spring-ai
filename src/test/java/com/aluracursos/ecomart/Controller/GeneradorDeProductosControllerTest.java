package com.aluracursos.ecomart.Controller;

import com.aluracursos.ecomart.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para GeneradorDeProductosController.
 * Ver CategorizadorDeProductosControllerTest para explicación de la estrategia de mocking.
 */
@WebMvcTest(controllers = GeneradorDeProductosController.class)
@Import({GlobalExceptionHandler.class, GeneradorDeProductosControllerTest.MockAiConfig.class})
class GeneradorDeProductosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatClient.CallResponseSpec callResponseSpec;

    @BeforeEach
    void resetMocks() {
        reset(callResponseSpec);
    }

    static class MockAiConfig {

        @Bean
        ChatClient.CallResponseSpec callResponseSpec() {
            return mock(ChatClient.CallResponseSpec.class);
        }

        @Bean
        ChatClient.Builder chatClientBuilder(ChatClient.CallResponseSpec callResponseSpec) {
            ChatClient.ChatClientRequestSpec promptSpec = mock(ChatClient.ChatClientRequestSpec.class);
            ChatClient chatClient = mock(ChatClient.class);
            ChatClient.Builder builder = mock(ChatClient.Builder.class);

            when(builder.defaultAdvisors(any(org.springframework.ai.chat.client.RequestResponseAdvisor[].class)))
                    .thenReturn(builder);
            when(builder.build()).thenReturn(chatClient);
            when(chatClient.prompt()).thenReturn(promptSpec);
            when(promptSpec.system(anyString())).thenReturn(promptSpec);
            when(promptSpec.user(anyString())).thenReturn(promptSpec);
            when(promptSpec.options(any())).thenReturn(promptSpec);
            when(promptSpec.call()).thenReturn(callResponseSpec);

            return builder;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Caso exitoso
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/generar-producto → 200 OK con nombre y descripcion")
    void generarDescripcionExitoso() throws Exception {
        when(callResponseSpec.content())
                .thenReturn("Cepillo biodegradable fabricado con bambú 100% natural.");

        mockMvc.perform(post("/api/v1/generar-producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Cepillo de dientes de bambú\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cepillo de dientes de bambú"))
                .andExpect(jsonPath("$.descripcion").value("Cepillo biodegradable fabricado con bambú 100% natural."));
    }

    @Test
    @DisplayName("POST /api/v1/generar-producto con respuesta nula del AI → devuelve descripcion vacía")
    void generarDescripcionConRespuestaNulaDelAI() throws Exception {
        when(callResponseSpec.content()).thenReturn(null);

        mockMvc.perform(post("/api/v1/generar-producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Producto sin descripción\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value(""));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Input inválido → 400
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/generar-producto con nombre vacío → 400 con mensaje de validación")
    void generarDescripcionNombreVacioDevuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/generar-producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre del producto no puede estar vacío"));
    }

    @Test
    @DisplayName("POST /api/v1/generar-producto con nombre en blanco → 400")
    void generarDescripcionNombreBlankDevuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/generar-producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre del producto no puede estar vacío"));
    }

    @Test
    @DisplayName("POST /api/v1/generar-producto sin body → 503 (HttpMessageNotReadableException cae en handler genérico)")
    void generarDescripcionSinBodyDevuelve503() throws Exception {
        mockMvc.perform(post("/api/v1/generar-producto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Excepción del servicio → GlobalExceptionHandler → 503
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/generar-producto cuando OpenAI falla → 503 con campo 'error'")
    void generarDescripcionExcepcionDevuelve503() throws Exception {
        when(callResponseSpec.content()).thenThrow(new RuntimeException("Timeout en OpenAI"));

        mockMvc.perform(post("/api/v1/generar-producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Cepillo de dientes de bambú\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Timeout en OpenAI"));
    }
}
