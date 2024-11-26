package me.songha.concert.reservation.pending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.messaging.KafkaProducerService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationPendingProducer {
    private static final String TOPIC_NAME = "reservation-topic";
    private final KafkaProducerService<ReservationPendingProducerRequest> kafkaProducerService;

    public void sendToQueue(String requestId, Long userId, Long concertId) {
        ReservationPendingProducerRequest request =
                new ReservationPendingProducerRequest(concertId, userId, requestId);

        kafkaProducerService.sendMessage(TOPIC_NAME, requestId, request);

        log.info("Sent reservation to Kafka. Request ID: {}", requestId);
    }

}