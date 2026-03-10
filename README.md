# Ecomart — API REST con Spring AI + OpenAI

![Java](https://img.shields.io/badge/Java-17-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen) ![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0--M2-orange) ![Maven](https://img.shields.io/badge/build-Maven-red)

## Descripción general

**Ecomart** es una API REST desarrollada como proyecto del curso **"Spring AI: Integra una aplicación con OpenAI"** de [Alura Latam / ONE](https://www.alura.com.br/one). El proyecto demuestra la integración de **Java 17 + Spring Boot 3** con los modelos de lenguaje e imagen de OpenAI a través de **Spring AI**.

La API permite a una tienda ecológica:

- **Categorizar** productos automáticamente con GPT-4o-mini.
- **Generar descripciones** de venta atractivas en lenguaje natural.
- **Crear imágenes** de producto con DALL-E 3.
- **Resumir** productos en 50 palabras con tono orientado a conversión.

Todas las operaciones incluyen **conteo de tokens de entrada** mediante JTokkit, lo que permite monitorear los costos de uso de la API de OpenAI desde el primer día.

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.3.x | Framework base |
| Spring Web | — | Capa REST |
| Spring Boot Actuator | — | Monitoreo y health-check |
| Spring AI (OpenAI) | 1.0.0-M2 | Integración con GPT y DALL-E |
| JTokkit | 1.1.0 | Conteo de tokens |
| Lombok | — | Reducción de boilerplate |
| Maven | — | Gestión de dependencias y build |

---

## Funcionalidades principales

- ✅ Categorización de productos con GPT-4o-mini
- ✅ Generación de descripciones de producto en español
- ✅ Resúmenes cortos (máx. 50 palabras) orientados a ventas
- ✅ Generación de imágenes con DALL-E 3
- ✅ Conteo de tokens de entrada por solicitud (control de costos)
- ✅ Validación de entrada con Jakarta Validation
- ✅ Manejo centralizado de errores con `@ControllerAdvice`

---

## Endpoints disponibles

| Método | URL | Descripción | Body (ejemplo) |
|---|---|---|---|
| `POST` | `/api/v1/categorizar` | Categoriza un producto ecológico con GPT | `{"nombre": "Jabón de avena orgánico"}` |
| `POST` | `/api/v1/generar-producto` | Genera una descripción de ficha de producto | `{"nombre": "Cepillo de dientes de bambú"}` |
| `POST` | `/api/v1/resumir` | Resume un producto en máx. 50 palabras | `{"nombre": "Botella de acero inoxidable"}` |
| `POST` | `/api/v1/imagen` | Genera una imagen del producto con DALL-E 3 | `{"prompt": "Imagen de bolsa de tela reutilizable ecológica"}` |

---

## Estructura del proyecto

```
ecomart/
├── src/
│   ├── main/
│   │   ├── java/com/aluracursos/ecomart/
│   │   │   ├── EcomartApplication.java
│   │   │   ├── Controller/
│   │   │   │   ├── CategorizadorDeProductosController.java
│   │   │   │   ├── GeneradorDeProductosController.java
│   │   │   │   ├── ResumenProductosController.java
│   │   │   │   └── GeneradorDeImagenesController.java
│   │   │   ├── dto/
│   │   │   │   ├── ProductoNombreRequest.java
│   │   │   │   ├── CategoriaResponse.java
│   │   │   │   ├── ProductoDescripcionResponse.java
│   │   │   │   ├── ProductoResumenResponse.java
│   │   │   │   ├── ImagenRequest.java
│   │   │   │   └── ImagenResponse.java
│   │   │   └── exception/
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

---

## Cómo ejecutar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/alemakey/ecomart-spring-ai.git
cd ecomart-spring-ai
```

### 2. Configurar la API Key de OpenAI

Crea una variable de entorno con tu clave de OpenAI:

**PowerShell (Windows):**

```powershell
$env:SPRING_AI_OPENAI_API_KEY = "sk-xxxxxxxxxxxxxxxxxxxxxxxx"
```

**Bash (Linux / macOS):**

```bash
export SPRING_AI_OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxx
```

### 3. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

O con Maven instalado globalmente:

```bash
mvn spring-boot:run
```

La API estará disponible en: **`http://localhost:8080`**

---

## Variables de entorno necesarias

| Variable | Descripción | Requerida |
|---|---|---|
| `SPRING_AI_OPENAI_API_KEY` | Clave de acceso a la API de OpenAI | ✅ Sí |

> ⚠️ **Nunca subas tu API key al repositorio.** El proyecto ya incluye `.gitignore` que excluye archivos `.env`.

---

## Ejemplos de requests

### POST `/api/v1/categorizar`

```json
{
  "nombre": "Jabón de avena orgánico sin parabenos"
}
```

**Respuesta:**

```json
{
  "nombre": "Jabón de avena orgánico sin parabenos",
  "categoria": "Higiene Natural",
  "tokensUsados": 18
}
```

---

### POST `/api/v1/generar-producto`

```json
{
  "nombre": "Cepillo de dientes de bambú"
}
```

**Respuesta:**

```json
{
  "nombre": "Cepillo de dientes de bambú",
  "descripcion": "Limpia con suavidad y cuida el planeta. Fabricado con bambú 100% biodegradable..."
}
```

---

### POST `/api/v1/resumir`

```json
{
  "nombre": "Botella de acero inoxidable reutilizable 750ml"
}
```

**Respuesta:**

```json
{
  "nombre": "Botella de acero inoxidable reutilizable 750ml",
  "resumen": "Mantén tus bebidas frías o calientes hasta 24 horas con esta botella de acero inoxidable...",
  "tokensUsados": 12
}
```

---

### POST `/api/v1/imagen`

```json
{
  "prompt": "Imagen profesional de una bolsa de tela ecológica de algodón orgánico"
}
```

**Respuesta:**

```json
{
  "url": "https://oaidalleapiprodscus.blob.core.windows.net/..."
}
```

---

## Próximas mejoras

- [ ] Agregar caché de respuestas para reducir costos de API
- [ ] Implementar rate limiting por IP
- [ ] Exponer métricas de tokens con Spring Boot Actuator
- [ ] Añadir soporte para embeddings y búsqueda semántica
- [ ] Dockerizar la aplicación

---

## Créditos

Este proyecto fue desarrollado como parte del curso **"Spring AI: Integra una aplicación con OpenAI"** de **[Alura Latam](https://www.alura.com.br/one)** / **Oracle Next Education (ONE)**.

- 📚 Plataforma: [alura.com.br](https://www.alura.com.br)
- 🤖 Modelos utilizados: GPT-4o-mini, DALL-E 3
- ☕ Tecnologías: Java 17, Spring Boot 3, Spring AI
