package me.songha.concert.reservation.pending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.concert.ConcertDto;
import me.songha.concert.concert.ConcertRepositoryService;
import me.songha.concert.reservation.general.ReservationDto;
import me.songha.concert.reservation.general.ReservationRepositoryService;
import me.songha.concert.reservation.general.ReservationStatus;
import me.songha.concert.reservation.history.ReservationHistoryRepositoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationPendingService {
    private final ReservationPendingRedisService reservationPendingRedisService;
    private final ReservationRepositoryService reservationRepositoryService;
    private final ReservationHistoryRepositoryService reservationHistoryRepositoryService;
    private final ConcertRepositoryService concertRepositoryService;

    public void processing(ReservationPendingProducerRequest request) {
        ConcertDto concertDto = concertRepositoryService.getConcert(request.getConcertId());

        ReservationDto reservationDto = ReservationDto.builder()
                .userId(request.getUserId())
                .concertId(concertDto.getId())
                .reservationStatus(ReservationStatus.PROCESSING.toString())
                .build();

        Long reservationId = reservationRepositoryService.createReservation(reservationDto);
        reservationPendingRedisService.saveReservationIdByRequestId(request.getRequestId(), reservationId);
        reservationPendingRedisService.updateStatus(request.getRequestId(), ReservationStatus.PROCESSING.toString());

        reservationHistoryRepositoryService.saveHistory(reservationId, request.getUserId(), 0, ReservationStatus.PROCESSING);
    }
}
