package com.example.OnlineMovieStreamingSystem.controller;

import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class HelloController {
    private final ImageStorageService imageStorageService;
    @GetMapping("/hello")
    @PreAuthorize("hasRole('ADMIN')")
    public String helloWorld() {
        return "Hello World";
    }

    @PostMapping("/images")
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile file) throws IOException {
        String urlImage = this.imageStorageService.uploadImage("artifact-image-container", file.getOriginalFilename(), file.getInputStream(), file.getSize());
        return ResponseEntity.ok().body(urlImage);
    }

    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteImage(@RequestParam String blobName) throws IOException {
        this.imageStorageService.deleteImage("artifact-image-container", blobName);
        return ResponseEntity.noContent().build();
    }
}
