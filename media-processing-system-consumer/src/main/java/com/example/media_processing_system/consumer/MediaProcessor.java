package com.example.media_processing_system.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.media_processing_system.service.LocalStorageService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class MediaProcessor {

    @Autowired
    private LocalStorageService localStorageService;
    
    @Value("${local.file.path}")
    private String localFilePath;

    @KafkaListener(topics = "media-processing", groupId = "media-processing-group")
    public void processFile(String fileName) {
        // Path to the input and output files
        File inputFile = new File(localFilePath + fileName);
        File outputFile = new File(localFilePath + fileName +"_converted");
        System.out.println(fileName + "   --------------------------------");
        try {
            System.out.println(fileName + "   ---sfewt3-----------------------------");
            convertVideoFormat(inputFile, outputFile);
            localStorageService.saveToLocal(fileName, "Processed content of file " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void convertVideoFormat(File inputFile, File outputFile) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "C:\\ffmpeg\\ffmpeg", "-i", inputFile.getAbsolutePath(), outputFile.getAbsolutePath()
        );
        processBuilder.redirectErrorStream(true); // Combine stdout and stderr
        Process process = processBuilder.start();

        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print ffmpeg output
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Process exited with non-zero status: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            throw new IOException("Process was interrupted", e);
        }
    }
}
