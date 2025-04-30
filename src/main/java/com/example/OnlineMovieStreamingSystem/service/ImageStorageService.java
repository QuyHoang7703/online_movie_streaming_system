package com.example.OnlineMovieStreamingSystem.service;

import java.io.IOException;
import java.io.InputStream;

public interface ImageStorageService {
    String uploadFile(String containerName, String originalImageName, InputStream data) throws IOException;
    void deleteFile(String containerName, String originalImageName) throws IOException;

}
