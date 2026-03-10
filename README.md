# Ecomart — API Spring Boot

Proyecto base Ecomart con Spring Boot 3.3.x y Java 17, generado para integrar Spring AI y OpenAI (GPT y DALL-E).

## Tecnologías

- Java 17
- Spring Boot 3.3.x
- Maven
- Spring Web
- Spring Boot Actuator
- Lombok

## Estructura del proyecto

```
ecomart/
├── src/
│   ├── main/
│   │   ├── java/com/aluracursos/ecomart/
│   │   │   ├── EcomartApplication.java
│   │   │   └── Controller/
│   │   │       ├── CategorizadorDeProductosController.java
│   │   │       ├── GeneradorDeProductosController.java
│   │   │       └── GeneradorDeImagenesController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/aluracursos/ecomart/
│           └── EcomartApplicationTests.java
└── pom.xml
```

## Endpoints de prueba

| Controller | Endpoint | Respuesta |
|---|---|---|
| CategorizadorDeProductos | GET `/categorizar/ping` | `ok - CategorizadorDeProductosController` |
| GeneradorDeProductos | GET `/generar-productos/ping` | `ok - GeneradorDeProductosController` |
| GeneradorDeImagenes | GET `/generar-imagenes/ping` | `ok - GeneradorDeImagenesController` |

## Cómo ejecutar

```bash
./mvnw spring-boot:run
```

O con Maven instalado localmente:

```bash
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

## Subir a GitHub

```bash
git init
git add .
git commit -m "Proyecto base Ecomart con Spring Boot"
git remote add origin https://github.com/mi-usuario/ecomart.git
git branch -M main
git push -u origin main
```
