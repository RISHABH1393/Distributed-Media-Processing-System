package com.example.media_processing_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${local.file.path}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        // Generate a unique file name
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + File.separator + fileName);

        // Ensure the directory exists
        Files.createDirectories(filePath.getParent());

        // Store the file
        Files.write(filePath, file.getBytes());
        
        // Process the file after storing it
        return processFile(filePath.toFile());

    }

public String processFile(File inputFile) {
    
    File compressedFile = new File(inputFile.getAbsolutePath() + "_compressed.mp4");

    try {
        // Convert and compress the video
        convertAndCompressVideo(inputFile, compressedFile);

    } catch (IOException e) {
        e.printStackTrace();
    }
    return compressedFile.getAbsolutePath();

}

private void convertAndCompressVideo(File inputFile, File compressedFile) throws IOException {
    // Using FFmpeg to convert the video to H.264 codec and compress it
    ProcessBuilder processBuilder = new ProcessBuilder(
        "C:\\ffmpeg\\ffmpeg", "-i", inputFile.getAbsolutePath(),
        "-vcodec", "libx264", // Using H.264 codec
        "-crf", "28", // Set compression level (lower number means higher quality)
        compressedFile.getAbsolutePath()
    );
    processBuilder.redirectErrorStream(true); // Combine stdout and stderr
    Process process = processBuilder.start();

    try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line); // Print FFmpeg output
        }
    }

    try {
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg process exited with non-zero status: " + exitCode);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // Restore interrupted status
        throw new IOException("Process was interrupted", e);
    }
}

}
