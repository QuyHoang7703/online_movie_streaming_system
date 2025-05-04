package com.example.OnlineMovieStreamingSystem.service.impl;

import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.example.OnlineMovieStreamingSystem.service.ImageStorageService;
import com.example.OnlineMovieStreamingSystem.util.exception.ApplicationException;
import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AzureImageStorageService implements ImageStorageService {
    // Automatically create object with config to connect storage account
    private final BlobServiceClient blobServiceClient;

    @Override
    public String uploadFile(String containerName, String originalFileName, InputStream data) throws IOException {
        try {
            // Đọc toàn bộ file vào byte array
            byte[] bytes = IOUtils.toByteArray(data);

            // Detect content type bằng Tika
            Tika tika = new Tika();
            String contentType = tika.detect(bytes, originalFileName);
            log.info("Content type: " + contentType);

            // Tạo lại InputStream từ byte array để upload
            InputStream uploadStream = new ByteArrayInputStream(bytes);

            // Tạo tên file mới
            String typeFile = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
            String newFileName = UUID.randomUUID().toString() + "." + typeFile;

            // Lấy BlobClient
            BlobContainerClient blobContainerClient = this.blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = blobContainerClient.getBlobClient(newFileName);

            // Set Content-Type đúng
            BlobHttpHeaders headers = new BlobHttpHeaders()
                    .setContentType(contentType);

            BlobParallelUploadOptions options = new BlobParallelUploadOptions(uploadStream)
                    .setHeaders(headers);

            blobClient.uploadWithResponse(options, null, Context.NONE);

            return blobClient.getBlobUrl();
        } catch (BlobStorageException e) {
            throw new ApplicationException("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String containerName, String originalImageName) throws IOException {
        try{
            BlobContainerClient blobContainerClient = this.blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = blobContainerClient.getBlobClient(originalImageName);
            if(blobClient.exists()){
                blobClient.delete();
                log.info("Successfully deleted image " + originalImageName);

            }else{
                log.error("Not found blob with name" + originalImageName);
            }
        }catch (BlobStorageException e){
            throw new ApplicationException("Failed to delete image: " + e.getMessage());
        }
    }

}
