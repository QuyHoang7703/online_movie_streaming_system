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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public String uploadImage(String containerName, String originalImageName, InputStream data) throws IOException {
        try{
           // Get the BlobContainerClient object to interact with the container
           BlobContainerClient blobContainerClient = this.blobServiceClient.getBlobContainerClient(containerName);

           String typeFile = originalImageName.substring(originalImageName.lastIndexOf(".") + 1); // Lấy phần mở rộng mà không có dấu "."
           String newImageName = UUID.randomUUID().toString() + "." + typeFile;  // Thêm dấu "." vào tên mới của file

           // Get the BlobClient object to interact with the specified blob
            BlobClient blobClient = blobContainerClient.getBlobClient(newImageName);
            BlobHttpHeaders headers = new BlobHttpHeaders()
                    .setContentType("image/" + typeFile); // Hoặc dùng Tika để tự detect loại file


            BlobParallelUploadOptions options = new BlobParallelUploadOptions(data)
                    .setHeaders(headers);

            blobClient.uploadWithResponse(options, null, Context.NONE);

           return blobClient.getBlobUrl();
       }catch (BlobStorageException e){
           throw new ApplicationException("Failed to upload image: " + e.getMessage());
       }

    }

    @Override
    public void deleteImage(String containerName, String originalImageName) throws IOException {
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
