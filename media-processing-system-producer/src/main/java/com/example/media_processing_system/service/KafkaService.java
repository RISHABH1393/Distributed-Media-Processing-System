package com.example.media_processing_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private static final String TOPIC = "media-processing";

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Autowired
    public KafkaService(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToKafka(byte[] file) {
        kafkaTemplate.send(TOPIC, file);
    }
}
