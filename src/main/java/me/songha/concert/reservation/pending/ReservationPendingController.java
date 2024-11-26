package me.songha.concert.reservation.pending;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.songha.concert.common.CurrentUser;
import me.songha.concert.concert.ConcertDto;
import me.songha.concert.concert.ConcertRepositoryService;
import me.songha.concert.reservation.general.ReservationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/reservation/pending")
@RequiredArgsConstructor
@RestController
public class ReservationPendingController {
    private final ReservationPendingProducer reservationPendingProducer;
    private final ReservationPendingRedisService reservationPendingRedisService;
    private final ConcertRepositoryService concertRepositoryService;

    @PostMapping
    public ResponseEntity<ReservationPendingResponse> pendingReservation(
            @CurrentUser Long userId, @Valid @RequestBody ReservationPendingRequest request) {
        String requestId = RequestNumberGenerator.generateRequestNumber(userId);

        ConcertDto concertDto = concertRepositoryService.getConcert(request.getConcertId());

        reservationPendingProducer.sendToQueue(requestId, userId, concertDto.getId());
        reservationPendingRedisService.saveStatus(requestId, ReservationStatus.PENDING.name());

        ReservationPendingResponse response = new ReservationPendingResponse(
                requestId,
                "Reservation request added to the queue.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/request-id/{requestId}/details")
    public ResponseEntity<ReservationDetailsResponse> getReservationDetails(@PathVariable String requestId) {
        String status = reservationPendingRedisService.getStatusByRequestId(requestId);
        if (status == null || status.equals(ReservationStatus.PENDING.name())) {
            return ResponseEntity.status(404).body(new ReservationDetailsResponse(requestId, "[Error] Request ID not found.", null));
        }
        String reservationId = reservationPendingRedisService.getReservationIdByRequestId(requestId);

        return ResponseEntity.ok(new ReservationDetailsResponse(requestId, status, reservationId));
    }

}
