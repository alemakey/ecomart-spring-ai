---
name: openai-cost-control
description: Skill para controlar y optimizar el uso de tokens y costos de la API de OpenAI en Ecomart. Actívame cuando el usuario pida optimizar costos, limitar tokens, revisar uso de la API, o implementar estrategias de ahorro.
version: 1.0.0
---

## Estrategias de control de costos

### 1. Limitar tokens en respuesta

```java
chatClient.prompt()
    .user(texto)
    .options(OpenAiChatOptions.builder()
        .withMaxTokens(150)  // Limitar respuesta
        .withTemperature(0.3f)  // Más determinístico = más eficiente
        .build())
    .call()
    .content();
```

### 2. Contar tokens ANTES de enviar (JTokkit)

```java
private static final int MAX_TOKENS_PERMITIDOS = 500;

public void validarTokens(String texto) {
    EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    Encoding enc = registry.getEncodingForModel(ModelType.GPT_4O_MINI);
    int tokens = enc.countTokens(texto);
    if (tokens > MAX_TOKENS_PERMITIDOS) {
        throw new IllegalArgumentException(
            "Texto demasiado largo: " + tokens + " tokens");
    }
}
```

### 3. Modelos por costo (menor a mayor)

| Modelo | Uso recomendado en Ecomart |
|---|---|
| gpt-4o-mini | Categorizar, describir productos |
| gpt-4o | Tareas complejas de razonamiento |
| dall-e-2 | Imágenes en pruebas/desarrollo |
| dall-e-3 | Imágenes en producción |

### 4. Caché de respuestas (evitar llamadas repetidas)

```java
@Service
public class ProductoCacheService {
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    
    public String obtenerOGenerarCategoria(String producto, ChatClient client) {
        return cache.computeIfAbsent(producto, k -> 
            client.prompt().user(k).call().content());
    }
}
```

## Alertas de costo recomendadas

- Configura límite de gasto en platform.openai.com
- Usa `gpt-4o-mini` por defecto, solo escala a `gpt-4o` si necesario
- Implementa conteo de tokens en todos los endpoints públicos
