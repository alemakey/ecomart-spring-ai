package com.aluracursos.ecomart.Controller;

import com.aluracursos.ecomart.dto.ProductoDescripcionResponse;
import com.aluracursos.ecomart.dto.ProductoNombreRequest;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint: POST /api/v1/generar-producto
 *
 * Genera una descripción de producto ecológico usando GPT-4o-mini.
 * Máximo 120 palabras, orientado a ficha de producto para ecommerce.
 */
@RestController
@RequestMapping("/api/v1")
@Validated
public class GeneradorDeProductosController {

        /**
         * ~180 tokens ≈ ~135 palabras en español — suficiente para el límite de 120
         * palabras.
         */
        private static final int MAX_TOKENS_RESPUESTA = 180;
        private static final float TEMPERATURA = 0.7f;

        private final ChatClient chatClient;

        public GeneradorDeProductosController(ChatClient.Builder builder) {
                this.chatClient = builder
                                .defaultAdvisors(new SimpleLoggerAdvisor())
                                .build();
        }

        @PostMapping("/generar-producto")
        public ResponseEntity<ProductoDescripcionResponse> generarDescripcion(
                        @Valid @RequestBody ProductoNombreRequest request) {

                String descripcion = chatClient.prompt()
                                .system("Eres un experto en copywriting para ecommerce ecológico y sostenible. "
                                                + "Genera descripciones atractivas, concisas y que resalten los beneficios ambientales. "
                                                + "La respuesta debe tener un máximo de 120 palabras, sin títulos ni bullets.")
                                .user("Genera una descripción de producto para: " + request.nombre())
                                .options(OpenAiChatOptions.builder()
                                                .withMaxTokens(MAX_TOKENS_RESPUESTA)
                                                .withTemperature((float) TEMPERATURA)
                                                .build())
                                .call()
                                .content();

                return ResponseEntity.ok(new ProductoDescripcionResponse(
                                request.nombre(),
                                descripcion != null ? descripcion.trim() : ""));
        }
}
