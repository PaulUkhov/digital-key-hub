package com.audio.storage.controller;

import com.audio.service.impl.MinioStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@Tag(name = "Storage Management", description = "API for file storage operations")
@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {
    private final MinioStorageService storageService;

    @Operation(
            summary = "Get avatar image",
            description = "Retrieves an avatar image by its object name from storage"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar image retrieved successfully",
                    content = @Content(mediaType = "image/jpeg",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Avatar not found")
    })
    @GetMapping("/avatar/{objectName}")
    public ResponseEntity<InputStreamResource> getAvatar(
            @Parameter(description = "Name of the avatar object in storage", required = true, example = "user123-avatar.jpg")
            @PathVariable("objectName") String objectName) {
        try {
            InputStream stream = storageService.getFile(objectName);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new InputStreamResource(stream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}