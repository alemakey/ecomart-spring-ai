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
 * Tests unitarios para CategorizadorDeProductosController.
 *
 * Estrategia de mocking:
 *   MockAiConfig provee un ChatClient.Builder completamente stubado como @Bean,
 *   que es construido por Spring antes de instanciar el controller (a diferencia de
 *   @BeforeEach que llegaría demasiado tarde).
 *
 *   El CallResponseSpec se expone como @Bean singleton para poder re-stubearlo
 *   en cada test, y se hace reset en @BeforeEach para evitar que el estado de
 *   stubs previos (p.ej. thenThrow) contamine tests posteriores.
 *
 * Nota sobre "sin body" → 503:
 *   Cuando el body está ausente, Spring lanza HttpMessageNotReadableException, que
 *   no es MethodArgumentNotValidException y por tanto cae en el handler genérico
 *   de GlobalExceptionHandler → 503. Esto es comportamiento real del sistema.
 */
@WebMvcTest(controllers = CategorizadorDeProductosController.class)
@Import({GlobalExceptionHandler.class, CategorizadorDeProductosControllerTest.MockAiConfig.class})
class CategorizadorDeProductosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatClient.CallResponseSpec callResponseSpec;

    @BeforeEach
    void resetMocks() {
        // Limpia stubs del test anterior para que cada test empiece con un mock
        // en estado neutro (sin thenReturn ni thenThrow heredados).
        reset(callResponseSpec);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Configuración del contexto de test
    // ─────────────────────────────────────────────────────────────────────────

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
    @DisplayName("POST /api/v1/categorizar → 200 OK con nombre, categoria y tokensUsados")
    void categorizarExitoso() throws Exception {
        when(callResponseSpec.content()).thenReturn("Higiene Natural");

        mockMvc.perform(post("/api/v1/categorizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Jabón de avena orgánico\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Jabón de avena orgánico"))
                .andExpect(jsonPath("$.categoria").value("Higiene Natural"))
                .andExpect(jsonPath("$.tokensUsados").isNumber());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Input inválido → @NotBlank → 400
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/categorizar con nombre vacío → 400 con mensaje de validación")
    void categorizarNombreVacioDevuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/categorizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre del producto no puede estar vacío"));
    }

    @Test
    @DisplayName("POST /api/v1/categorizar con nombre en blanco (solo espacios) → 400")
    void categorizarNombreBlankDevuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/categorizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre del producto no puede estar vacío"));
    }

    @Test
    @DisplayName("POST /api/v1/categorizar sin body → 503 (HttpMessageNotReadableException cae en handler genérico)")
    void categorizarSinBodyDevuelve503() throws Exception {
        // Body ausente → Spring lanza HttpMessageNotReadableException (no es
        // MethodArgumentNotValidException) → GlobalExceptionHandler genérico → 503.
        mockMvc.perform(post("/api/v1/categorizar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Excepción del servicio → GlobalExceptionHandler → 503
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/categorizar cuando OpenAI falla → 503 con campo 'error'")
    void categorizarExcepcionDevuelve503() throws Exception {
        when(callResponseSpec.content()).thenThrow(new RuntimeException("OpenAI no disponible"));

        mockMvc.perform(post("/api/v1/categorizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Jabón de avena orgánico\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("OpenAI no disponible"));
    }
}
