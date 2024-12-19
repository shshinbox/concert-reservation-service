package me.songha.concert.reservation.pending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.reservation.common.ReservationProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationPendingConsumer {
    private final ReservationPendingService reservationPendingService;

    @KafkaListener(
            topics = ReservationProperties.RESERVATION_TOPIC,
            groupId = ReservationProperties.RESERVATION_GROUP_ID,
            concurrency = ReservationProperties.RESERVATION_CONCURRENCY,
            containerFactory = "reservationKafkaListenerContainerFactory")
    public void consume(List<ConsumerRecord<String, ReservationPendingProducerRequest>> records) {
        for (ConsumerRecord<String, ReservationPendingProducerRequest> record : records) {
            reservationPendingService.processing(record.value());
        }
        log.info("Batch processed successfully. size: {}", records.size());
    }
}