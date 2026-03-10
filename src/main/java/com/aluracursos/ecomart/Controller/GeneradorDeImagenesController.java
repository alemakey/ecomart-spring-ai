package com.aluracursos.ecomart.Controller;

import com.aluracursos.ecomart.dto.ImagenRequest;
import com.aluracursos.ecomart.dto.ImagenResponse;
import jakarta.validation.Valid;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint: POST /api/v1/imagen
 *
 * Genera una imagen con DALL-E 3 a partir de un prompt descriptivo.
 * Devuelve la URL pública de la imagen generada.
 */
@RestController
@RequestMapping("/api/v1")
@Validated
public class GeneradorDeImagenesController {

    private final ImageModel imageModel;

    public GeneradorDeImagenesController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @PostMapping("/imagen")
    public ResponseEntity<ImagenResponse> generarImagen(
            @Valid @RequestBody ImagenRequest request) {

        ImageResponse response = imageModel.call(
                new ImagePrompt(
                        request.prompt(),
                        OpenAiImageOptions.builder()
                                .withModel("dall-e-3")
                                .withQuality("standard")
                                .withN(1)
                                .withHeight(1024)
                                .withWidth(1024)
                                .build()));

        String url = response.getResult().getOutput().getUrl();
        return ResponseEntity.ok(new ImagenResponse(url));
    }
}
