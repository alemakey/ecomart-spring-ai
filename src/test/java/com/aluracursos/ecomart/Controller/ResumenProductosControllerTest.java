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
 * Tests unitarios para ResumenProductosController.
 * Ver CategorizadorDeProductosControllerTest para explicación de la estrategia de mocking.
 */
@WebMvcTest(controllers = ResumenProductosController.class)
@Import({GlobalExceptionHandler.class, ResumenProductosControllerTest.MockAiConfig.class})
class ResumenProductosControllerTest {

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
    @DisplayName("POST /api/v1/resumir → 200 OK con nombre, resumen y tokensUsados")
    void resumirExitoso() throws Exception {
        when(callResponseSpec.content())
                .thenReturn("Botella reutilizable de acero que mantiene tu bebida a temperatura ideal.");

        mockMvc.perform(post("/api/v1/resumir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Botella de acero inoxidable reutilizable 750ml\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Botella de acero inoxidable reutilizable 750ml"))
                .andExpect(jsonPath("$.resumen").value("Botella reutilizable de acero que mantiene tu bebida a temperatura ideal."))
                .andExpect(jsonPath("$.tokensUsados").isNumber());
    }

    @Test
    @DisplayName("POST /api/v1/resumir con respuesta nula del AI → devuelve resumen vacío")
    void resumirConRespuestaNulaDelAI() throws Exception {
        when(callResponseSpec.content()).thenReturn(null);

        mockMvc.perform(post("/api/v1/resumir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Producto sin resumen\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumen").value(""));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Input inválido → 400
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/resumir con nombre vacío → 400 con mensaje de validación")
    void resumirNombreVacioDevuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/resumir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre del producto no puede estar vacío"));
    }

    @Test
    @DisplayName("POST /api/v1/resumir con nombre en blanco (solo espacios) → 400")
    void resumirNombreBlankDevuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/resumir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre del producto no puede estar vacío"));
    }

    @Test
    @DisplayName("POST /api/v1/resumir sin body → 503 (HttpMessageNotReadableException cae en handler genérico)")
    void resumirSinBodyDevuelve503() throws Exception {
        mockMvc.perform(post("/api/v1/resumir")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Excepción del servicio → GlobalExceptionHandler → 503
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/resumir cuando OpenAI falla → 503 con campo 'error'")
    void resumirExcepcionDevuelve503() throws Exception {
        when(callResponseSpec.content()).thenThrow(new RuntimeException("Servicio de IA no disponible"));

        mockMvc.perform(post("/api/v1/resumir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Botella de acero inoxidable reutilizable 750ml\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Servicio de IA no disponible"));
    }
}
