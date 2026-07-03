package com.aluracursos.ecomart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de humo: verifica que el contexto de Spring arranca correctamente.
 *
 * Spring AI auto-configura ChatClient.Builder e ImageModel usando el valor ficticio
 * de spring.ai.openai.api-key definido en maven-surefire-plugin (pom.xml).
 * Los beans se construyen sin llamadas de red → el contexto carga sin API key real.
 *
 * No se usa @MockBean aquí: con @MockBean el ChatClient.Builder mockeado tendría
 * stubs vacíos y fallaría cuando el controller lo use en su constructor.
 */
@SpringBootTest
class EcomartApplicationTests {

    @Test
    void contextLoads() {
    }

}
