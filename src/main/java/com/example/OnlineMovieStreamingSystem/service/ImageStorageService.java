package com.example.OnlineMovieStreamingSystem.service;

import java.io.IOException;
import java.io.InputStream;

public interface ImageStorageService {
    String uploadImage(String containerName, String originalImageName, InputStream data) throws IOException;
    void deleteImage(String containerName, String originalImageName) throws IOException;
}
