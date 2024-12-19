package me.songha.concert.shared.messaging;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;


@Service
public class KafkaProducerService<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String key, T value) {
        kafkaTemplate.send(topic, key, value);
    }

    public RecordMetadata sendMessageAndReturn(String topic, String key, T value) throws ExecutionException, InterruptedException {
        SendResult<String, Object> result = kafkaTemplate.send(topic, key, value).get();
        return result.getRecordMetadata();
    }
}