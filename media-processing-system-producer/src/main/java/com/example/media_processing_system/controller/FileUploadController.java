package com.example.media_processing_system.controller;

import java.io.IOException;
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

            // Send file name to Kafka
            kafkaService.sendToKafka(fileName);

            return ResponseEntity.ok("File uploaded and message sent to Kafka successfully. File name: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }
}

