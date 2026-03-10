---
name: ecomart-api
description: Skill para desarrollar y extender la API REST Ecomart con Spring AI y OpenAI. Actívame cuando el usuario pida crear endpoints, controladores, integrar GPT, DALL-E, embeddings, manejo de errores o cualquier funcionalidad de la API Ecomart.
version: 1.0.0
---

## Descripción del proyecto

Ecomart es una API REST en Java 17 + Spring Boot 3.3.4 + Spring AI 1.0.0-M2 que integra OpenAI (GPT y DALL-E) para categorizar productos, generar descripciones e imágenes.

## Estructura del proyecto

- Paquete base: `com.aluracursos.ecomart`
- Controllers: `src/main/java/com/aluracursos/ecomart/Controller/`
- Config: `src/main/resources/application.properties`
- Puerto: 8080

## Controladores existentes

| Controlador | Endpoint | Función |
|---|---|---|
| CategorizadorDeProductosController | POST /categorizar | Categoriza con GPT |
| GeneradorDeProductosController | POST /generar-producto | Genera descripción con GPT |
| GeneradorDeImagenesController | POST /imagen | Genera imagen con DALL-E |

## Instrucciones para el agente

### Al crear un nuevo endpoint

1. Crear clase en `Controller/` con sufijo `Controller.java`
2. Anotar con `@RestController` y `@RequestMapping`
3. Usar `ChatClient` para interacciones con GPT
4. Usar `ImageModel` para generación de imágenes con DALL-E
5. Usar `EmbeddingModel` para embeddings
6. Seguir el patrón REST ya establecido
7. NUNCA hardcodear la API key

### Patrón base de un Controller

```java
@RestController
public class NuevoController {
    
    private final ChatClient chatClient;
    
    public NuevoController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    
    @PostMapping("/nuevo-endpoint")
    public String procesar(@RequestBody Map<String, String> body) {
        return chatClient.prompt()
            .user(body.get("nombre"))
            .call()
            .content();
    }
}
```

## Dependencias disponibles (pom.xml)

- `spring-ai-openai-spring-boot-starter` — Spring AI
- `jtokkit:1.1.0` — conteo de tokens
- `SimpleLoggerAdvisor` — logging de solicitudes

## Configuración (application.properties)

```
spring.ai.openai.api-key=${SPRING_AI_OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.openai.image.options.model=dall-e-3
```

## Referencias

- Spring AI Docs: <https://docs.spring.io/spring-ai/reference/>
- OpenAI API: <https://platform.openai.com/docs>
- Repositorio: <https://github.com/alura-es-cursos/2112-GPT-y-Java-integra-una-aplicacion-con-OpenAI>
