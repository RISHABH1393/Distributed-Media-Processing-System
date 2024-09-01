package com.example.media_processing_system.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class LocalStorageService {

    @Value("${local.file.path}")
    private String localFilePath;

    public void saveToLocal(String fileName, String content) {
        File file = new File(localFilePath + fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
