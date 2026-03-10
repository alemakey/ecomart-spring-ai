---
name: java-best-practices
description: Skill de convenciones Java 17 y Spring Boot 3.x para el proyecto Ecomart. Actívame cuando el usuario pida refactorizar código, crear DTOs, validaciones, tests, documentación o aplicar patrones de diseño en el proyecto.
version: 1.0.0
---

## Convenciones del proyecto Ecomart

### Nomenclatura

- Controllers: `NombreController.java` en PascalCase
- DTOs: `NombreRequest.java` y `NombreResponse.java`
- Services: `NombreService.java`
- Paquete base: `com.aluracursos.ecomart`

### DTO de Request recomendado

```java
public record ProductoRequest(
    @NotBlank(message = "El nombre no puede estar vacío")
    String nombre
) {}
```

### DTO de Response recomendado

```java
public record ProductoResponse(
    String nombre,
    String categoria,
    String descripcion,
    int tokensUsados
) {}
```

### Estructura recomendada de Controller

```java
@RestController
@RequestMapping("/api/v1")
@Validated
public class EjemploController {

    private final ChatClient chatClient;

    public EjemploController(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultAdvisors(new SimpleLoggerAdvisor())
            .build();
    }

    @PostMapping("/endpoint")
    public ResponseEntity<ProductoResponse> procesar(
            @Valid @RequestBody ProductoRequest request) {
        // lógica aquí
        return ResponseEntity.ok(response);
    }
}
```

### Test básico de Controller

```java
@SpringBootTest
@AutoConfigureMockMvc
class CategorizadorControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    void deberiaCategorizar() throws Exception {
        mockMvc.perform(post("/categorizar")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"nombre": "Camiseta algodón orgánico"}"""))
            .andExpect(status().isOk());
    }
}
```

## Instrucciones generales

- Usar Java records para DTOs inmutables
- Siempre validar entrada con `@Valid` + `@NotBlank`
- Retornar `ResponseEntity<>` en todos los endpoints
- Separar lógica en Services cuando crezca el Controller
- Versionar la API desde el inicio (`/api/v1/`)
