---
name: spring-ai-expert
description: Skill de patrones avanzados de Spring AI. Actívame cuando el usuario pida implementar ChatClient, ImageModel, EmbeddingModel, Advisors, retry automático, streaming, prompts con roles, o cualquier configuración avanzada de Spring AI 1.0.0-M2.
version: 1.0.0
---

## Patrones Spring AI para Ecomart

### ChatClient con SystemPrompt

```java
ChatClient.builder(chatModel)
    .defaultSystem("Eres un experto en productos ecológicos.")
    .build();
```

### Prompt con roles

```java
chatClient.prompt()
    .system("Eres asistente de ecommerce ecológico")
    .user("Categoriza: " + nombreProducto)
    .call()
    .content();
```

### SimpleLoggerAdvisor (logging)

```java
ChatClient.builder(chatModel)
    .defaultAdvisors(new SimpleLoggerAdvisor())
    .build();
```

### Retry automático

```
spring.ai.retry.max-attempts=3
spring.ai.retry.backoff.initial-interval=2000
spring.ai.retry.backoff.multiplier=2
```

### Generación de imagen con DALL-E

```java
@Autowired
private ImageModel imageModel;

ImageResponse response = imageModel.call(
    new ImagePrompt(prompt,
        OpenAiImageOptions.builder()
            .withModel("dall-e-3")
            .withQuality("standard")
            .withN(1)
            .withHeight(1024)
            .withWidth(1024)
            .build()
    )
);
String imageUrl = response.getResult().getOutput().getUrl();
```

### Embeddings para búsqueda semántica

```java
@Autowired
private EmbeddingModel embeddingModel;

float[] vector = embeddingModel.embed("texto a vectorizar");
```

### Conteo de tokens con JTokkit

```java
EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
Encoding encoding = registry.getEncodingForModel(ModelType.GPT_4O_MINI);
int tokenCount = encoding.countTokens(texto);
```

### Manejo de errores global

```java
@ControllerAdvice
public class OpenAiExceptionHandler {
    @ExceptionHandler(OpenAiHttpException.class)
    public ResponseEntity<String> handleOpenAiError(OpenAiHttpException e) {
        return ResponseEntity.status(503)
            .body("Error OpenAI: " + e.getMessage());
    }
}
```

## Reglas importantes

- Siempre usar variables de entorno para la API key
- Configurar límites de tokens para controlar costos
- No loguear prompts con datos sensibles en producción
- Usar `gpt-4o-mini` para tareas de categorización (más económico)
- Usar `dall-e-3` solo cuando se necesite alta calidad de imagen
