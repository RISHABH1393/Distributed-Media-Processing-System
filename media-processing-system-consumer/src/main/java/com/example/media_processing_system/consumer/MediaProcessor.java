package com.example.media_processing_system.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.media_processing_system.service.LocalStorageService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class MediaProcessor {

    @Autowired
    private LocalStorageService localStorageService;

    @Value("${local.file.path}")
    private String localFilePath;

    @KafkaListener(topics = "media-processing", groupId = "media-processing-group")
    public void processFile(byte[] fileBytes) {
        // Output file name for the processed video
        String outputFileName = localFilePath + "processed_video.mp4"; // Change to desired format if needed
        File outputFile = new File(outputFileName);
        
        System.out.println("Processing received file...");

        try {
            // Save the received bytes to a file
            saveFile(outputFile, fileBytes);

            // Convert the video format after saving
            convertVideoFormat(outputFile);

            // Save or perform further actions with the processed file
            localStorageService.saveToLocal(outputFileName, "Processed content of the video");
            System.out.println("File processed and saved: " + outputFileName);
            
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveFile(File file, byte[] fileBytes) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileBytes);
            System.out.println("File saved: " + file.getAbsolutePath());
        }
    }

    private void convertVideoFormat(File inputFile) throws IOException {
        // Define the output file for conversion
        File convertedFile = new File(inputFile.getAbsolutePath().replace(".mp4", "_converted.mp4"));

        // Command to run FFmpeg to convert the video
        ProcessBuilder processBuilder = new ProcessBuilder(
            "C:\\ffmpeg\\ffmpeg", "-i", inputFile.getAbsolutePath(),
            "-vcodec", "libx264", // Using H.264 codec
            "-crf", "28", // Compression level (lower is higher quality)
            convertedFile.getAbsolutePath()
        );

        processBuilder.redirectErrorStream(true); // Combine stdout and stderr
        Process process = processBuilder.start();

        // Read and print FFmpeg output for logging purposes
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
