# 🌿 Ecomart — API REST con Spring AI + OpenAI

> API REST desarrollada en **Java 17 + Spring Boot 3** que integra **Spring AI** y **OpenAI** (GPT-4o-mini y DALL-E 3) para categorizar productos, generar descripciones, resumir fichas e ilustrar artículos de un catálogo de ecommerce ecológico.
>
> Desarrollada como challenge del programa **Oracle Next Education (ONE) G9** · Alura Latam.

---

## 🗂️ Funcionalidades

- 🏷️ **Categorización automática** de productos con GPT-4o-mini
- 📝 **Generación de descripciones** de ficha de producto (máx. 120 palabras)
- ✍️ **Resúmenes cortos** orientados a ventas (máx. 50 palabras)
- 🖼️ **Generación de imágenes** de producto con DALL-E 3
- 🔢 **Conteo de tokens de entrada** por solicitud para control de costos (JTokkit)
- ✅ **Validación de entrada** con Jakarta Validation (`@Valid` + `@NotBlank`)
- 🛡️ **Manejo centralizado de errores** con `@ControllerAdvice`

---

## 📡 Endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| `POST` | `/api/v1/categorizar` | Categoriza un producto ecológico con GPT |
| `POST` | `/api/v1/generar-producto` | Genera una descripción de ficha de producto |
| `POST` | `/api/v1/resumir` | Resume el producto en máx. 50 palabras |
| `POST` | `/api/v1/imagen` | Genera una imagen del producto con DALL-E 3 |

### Ejemplo — Categorizar producto

```http
POST /api/v1/categorizar
Content-Type: application/json

{ "nombre": "Jabón de avena orgánico sin parabenos" }
```

```json
{
  "nombre": "Jabón de avena orgánico sin parabenos",
  "categoria": "Higiene Natural",
  "tokensUsados": 18
}
```

### Ejemplo — Generar descripción

```http
POST /api/v1/generar-producto
Content-Type: application/json

{ "nombre": "Cepillo de dientes de bambú" }
```

```json
{
  "nombre": "Cepillo de dientes de bambú",
  "descripcion": "Limpia con suavidad y cuida el planeta. Fabricado con bambú 100% biodegradable..."
}
```

### Ejemplo — Resumir producto

```http
POST /api/v1/resumir
Content-Type: application/json

{ "nombre": "Botella de acero inoxidable reutilizable 750ml" }
```

```json
{
  "nombre": "Botella de acero inoxidable reutilizable 750ml",
  "resumen": "Mantén tus bebidas frías o calientes hasta 24 horas con esta botella de acero inoxidable...",
  "tokensUsados": 12
}
```

### Ejemplo — Generar imagen

```http
POST /api/v1/imagen
Content-Type: application/json

{ "prompt": "Bolsa de tela ecológica de algodón orgánico, fondo blanco, estilo profesional" }
```

```json
{
  "url": "https://oaidalleapiprodscus.blob.core.windows.net/..."
}
```

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| **Java** | 17 | Lenguaje principal |
| **Spring Boot** | 3.3.x | Framework base |
| **Spring Web** | — | Capa REST |
| **Spring Boot Actuator** | — | Monitoreo y health-check |
| **Spring AI (OpenAI)** | 1.0.0-M2 | Integración con GPT y DALL-E |
| **JTokkit** | 1.1.0 | Conteo de tokens |
| **Lombok** | — | Reducción de boilerplate |
| **Maven Wrapper** | — | No requiere Maven instalado |

---

## 🚀 Cómo ejecutar el proyecto localmente

### Requisitos previos

- **JDK 17 o superior** instalado ([descargar aquí](https://www.oracle.com/java/technologies/downloads/))
- **Git** instalado
- **Clave de API de OpenAI** ([obtener aquí](https://platform.openai.com/api-keys))

### Pasos

**1. Clonar el repositorio**

```bash
git clone https://github.com/alemakey/ecomart-spring-ai.git
cd ecomart-spring-ai
```

**2. Configurar la variable de entorno con tu API Key**

En Windows (PowerShell):

```powershell
$env:SPRING_AI_OPENAI_API_KEY = "sk-xxxxxxxxxxxxxxxxxxxx"
```

En macOS / Linux:

```bash
export SPRING_AI_OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxx
```

> ⚠️ **Nunca subas tu API key al repositorio.** El `.gitignore` ya excluye archivos `.env` y `application-secrets.properties`.

**3. Ejecutar la aplicación**

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En macOS / Linux:

```bash
./mvnw spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

---

## 📁 Estructura del proyecto

```
src/
└── main/
    ├── java/com/aluracursos/ecomart/
    │   ├── EcomartApplication.java
    │   ├── Controller/
    │   │   ├── CategorizadorDeProductosController.java   # POST /api/v1/categorizar
    │   │   ├── GeneradorDeProductosController.java       # POST /api/v1/generar-producto
    │   │   ├── ResumenProductosController.java           # POST /api/v1/resumir
    │   │   └── GeneradorDeImagenesController.java        # POST /api/v1/imagen
    │   ├── dto/
    │   │   ├── ProductoNombreRequest.java                # Request compartido
    │   │   ├── CategoriaResponse.java
    │   │   ├── ProductoDescripcionResponse.java
    │   │   ├── ProductoResumenResponse.java
    │   │   ├── ImagenRequest.java
    │   │   └── ImagenResponse.java
    │   └── exception/
    │       └── GlobalExceptionHandler.java               # Manejo global de errores
    └── resources/
        └── application.properties
```

---

## ✒️ Autor

Desarrollado con ❤️ por **Victor Martinez Reyna**
📌 Programa **Oracle Next Education (ONE) G9** · Alura Latam
