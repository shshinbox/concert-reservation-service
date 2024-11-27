package me.songha.concert.reservation.pending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.common.exception.NotFoundException;
import me.songha.concert.common.exception.ReservationIllegalArgumentException;
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
            topics = "reservation-topic",
            groupId = "reservation-group",
            concurrency = "3",
            containerFactory = "reservationKafkaListenerContainerFactory")
    public void consume(List<ConsumerRecord<String, ReservationPendingProducerRequest>> records) {
        int success = 0, failed = 0;
        for (ConsumerRecord<String, ReservationPendingProducerRequest> record : records) {
            try {
                reservationPendingService.processing(record.value());
                success++;

            } catch (ReservationIllegalArgumentException | NotFoundException ex) {
                log.error("[Error] Failed to process reservation. e.getMessage():{}", ex.getMessage(), ex);
                failed++;

            } catch (Exception e) {
                log.error("[Error] Temporary error for message {}. Sent through DefaultErrorHandler.", record.value(), e);
                failed++;
            }
        }
        log.info("Batch processed successfully. Success: {}, Failed: {}", success, failed);
    }
}