package com.example.media_processing_system.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.example.media_processing_system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

    
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private KafkaService kafkaService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Store the file locally
            String fileName = fileStorageService.storeFile(file);
            
            // Read the stored file as bytes
            Path path = Path.of(fileName); 
            // Send file  to Kafka
             byte[] fileBytes = Files.readAllBytes(path);
            kafkaService.sendToKafka(fileBytes);

            return ResponseEntity.ok("File uploaded and message sent to Kafka successfully. File name: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }
}

