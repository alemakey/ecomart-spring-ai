package com.aluracursos.ecomart.Controller;

import com.aluracursos.ecomart.dto.CategoriaResponse;
import com.aluracursos.ecomart.dto.ProductoNombreRequest;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
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
 * Endpoint: POST /api/v1/categorizar
 *
 * Categoriza un producto ecológico usando GPT-4o-mini.
 * Incluye conteo de tokens de entrada via JTokkit para visibilidad de costos.
 */
@RestController
@RequestMapping("/api/v1")
@Validated
public class CategorizadorDeProductosController {

        private static final int MAX_TOKENS_RESPUESTA = 100;
        private static final float TEMPERATURA = 0.3f;

        private final ChatClient chatClient;
        private final EncodingRegistry encodingRegistry;

        public CategorizadorDeProductosController(ChatClient.Builder builder) {
                this.chatClient = builder
                                .defaultAdvisors(new SimpleLoggerAdvisor())
                                .build();
                this.encodingRegistry = Encodings.newDefaultEncodingRegistry();
        }

        @PostMapping("/categorizar")
        public ResponseEntity<CategoriaResponse> categorizar(
                        @Valid @RequestBody ProductoNombreRequest request) {

                String userPrompt = "Categoriza el siguiente producto de ecommerce ecológico "
                                + "en una sola categoría concisa (máximo 3 palabras): " + request.nombre();

                // Conteo de tokens de entrada antes de enviar la solicitud
                Encoding encoding = encodingRegistry.getEncodingForModel(ModelType.GPT_4O_MINI);
                int tokensEntrada = encoding.countTokens(userPrompt);

                String categoria = chatClient.prompt()
                                .system("Eres un experto en clasificación de productos ecológicos y sostenibles. "
                                                + "Responde ÚNICAMENTE con el nombre de la categoría, sin explicaciones.")
                                .user(userPrompt)
                                .options(OpenAiChatOptions.builder()
                                                .withMaxTokens(MAX_TOKENS_RESPUESTA)
                                                .withTemperature((float) TEMPERATURA)
                                                .build())
                                .call()
                                .content();

                return ResponseEntity.ok(new CategoriaResponse(
                                request.nombre(),
                                categoria != null ? categoria.trim() : "",
                                tokensEntrada));
        }
}
