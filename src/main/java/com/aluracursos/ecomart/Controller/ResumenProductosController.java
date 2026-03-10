package com.aluracursos.ecomart.Controller;

import com.aluracursos.ecomart.dto.ProductoNombreRequest;
import com.aluracursos.ecomart.dto.ProductoResumenResponse;
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
 * Endpoint: POST /api/v1/resumir
 *
 * Genera un resumen de máximo 50 palabras para un producto ecológico usando
 * GPT-4o-mini.
 * Incluye conteo de tokens de entrada via JTokkit para visibilidad de costos.
 */
@RestController
@RequestMapping("/api/v1")
@Validated
public class ResumenProductosController {

    // ~120 tokens ≈ ~90 palabras en español — suficiente para el límite de 50
    // palabras.
    private static final int MAX_TOKENS_RESPUESTA = 120;
    private static final float TEMPERATURA = 0.4f;

    private final ChatClient chatClient;
    private final EncodingRegistry encodingRegistry;

    public ResumenProductosController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.encodingRegistry = Encodings.newDefaultEncodingRegistry();
    }

    @PostMapping("/resumir")
    public ResponseEntity<ProductoResumenResponse> resumir(
            @Valid @RequestBody ProductoNombreRequest request) {

        String userPrompt = request.nombre();

        // Conteo de tokens de entrada antes de enviar la solicitud
        Encoding encoding = encodingRegistry.getEncodingForModel(ModelType.GPT_4O_MINI);
        int tokensEntrada = encoding.countTokens(userPrompt);

        String resumen = chatClient.prompt()
                .system("Eres un experto en ecommerce. Resume el producto en un máximo de 50 palabras "
                        + "en español, en tono claro y vendedor.")
                .user(userPrompt)
                .options(OpenAiChatOptions.builder()
                        .withMaxTokens(MAX_TOKENS_RESPUESTA)
                        .withTemperature(TEMPERATURA)
                        .build())
                .call()
                .content();

        return ResponseEntity.ok(new ProductoResumenResponse(
                request.nombre(),
                resumen != null ? resumen.trim() : "",
                tokensEntrada));
    }
}
