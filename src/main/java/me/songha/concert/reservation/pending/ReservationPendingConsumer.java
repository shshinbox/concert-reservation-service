package me.songha.concert.reservation.pending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.reservation.general.ReservationDto;
import me.songha.concert.reservation.general.ReservationRepositoryService;
import me.songha.concert.reservation.general.ReservationStatus;
import me.songha.concert.reservation.history.ReservationHistoryRepositoryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationPendingConsumer {
    private final ReservationPendingRedisService reservationPendingRedisService;
    private final ReservationRepositoryService reservationRepositoryService;
    private final ReservationHistoryRepositoryService reservationHistoryRepositoryService;

    @KafkaListener(topics = "reservation-topic", groupId = "reservation-group")
    public void consume(ConsumerRecord<String, ReservationPendingProducerRequest> record) {
        try {
            ReservationPendingProducerRequest request = record.value();

            ReservationDto reservationDto = ReservationDto.builder()
                    .userId(request.getUserId())
                    .concertId(request.getConcertId())
                    .reservationStatus(ReservationStatus.PROCESSING.toString())
                    .build();

            Long reservationId = reservationRepositoryService.createReservation(reservationDto);
            reservationPendingRedisService.saveReservationIdByRequestId(request.getRequestId(), reservationId);
            reservationPendingRedisService.updateStatus(request.getRequestId(), ReservationStatus.PROCESSING.toString());

            reservationHistoryRepositoryService.saveHistory(reservationId, request.getUserId(), 0, ReservationStatus.PROCESSING);

        } catch (Exception e) {
            log.error("[Error] Failed to process reservation. e.getMessage():{}", e.getMessage(), e);
            throw e;
        }
    }
}