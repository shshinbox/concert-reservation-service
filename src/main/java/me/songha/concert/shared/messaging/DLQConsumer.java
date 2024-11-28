package me.songha.concert.shared.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class DLQConsumer {

    @KafkaListener(
            topics = "dlq-topic",
            groupId = "dlq-group",
            concurrency = "3",
            containerFactory = "genericKafkaListenerContainerFactory")
    public void processDlqMessages(List<ConsumerRecord<String, Object>> records) {
        for (ConsumerRecord<String, Object> record : records) {
            log.info("Processing DLQ message:{}", record.value());
        }
    }
}