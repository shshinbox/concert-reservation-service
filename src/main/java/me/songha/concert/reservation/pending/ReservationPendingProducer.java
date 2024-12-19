package me.songha.concert.reservation.pending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.concert.ConcertDto;
import me.songha.concert.concert.ConcertRepositoryService;
import me.songha.concert.reservation.common.ReservationProperties;
import me.songha.concert.shared.messaging.KafkaProducerService;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationPendingProducer {
    private final KafkaProducerService<ReservationPendingProducerRequest> kafkaProducerService;
    private final ConcertRepositoryService concertRepositoryService;

    public void send(String requestId, Long userId, Long concertId) {
        ConcertDto concertDto = concertRepositoryService.getConcert(concertId);

        ReservationPendingProducerRequest request =
                new ReservationPendingProducerRequest(concertDto.getId(), userId, requestId);

        kafkaProducerService.sendMessage(ReservationProperties.RESERVATION_TOPIC, requestId, request);

        log.info("Sent reservation to Kafka. Request ID: {}", requestId);
    }

    public long[] sendAndReturnMetadata(String requestId, Long userId, Long concertId) throws ExecutionException, InterruptedException {
        ConcertDto concertDto = concertRepositoryService.getConcert(concertId);
        ReservationPendingProducerRequest request =
                new ReservationPendingProducerRequest(concertDto.getId(), userId, requestId);

        RecordMetadata metadata = kafkaProducerService.sendMessageAndReturn(ReservationProperties.RESERVATION_TOPIC, requestId, request);
        int partition = metadata.partition();
        long offset = metadata.offset();

        return new long[]{partition, offset};
    }
}