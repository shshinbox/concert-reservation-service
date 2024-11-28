package me.songha.concert.reservation.pending;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.songha.concert.shared.security.CurrentUser;
import me.songha.concert.shared.util.RequestNumberGenerator;
import me.songha.concert.reservation.general.ReservationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/reservation/pending")
@RequiredArgsConstructor
@RestController
public class ReservationPendingController {
    private final ReservationPendingProducer reservationPendingProducer;
    private final ReservationPendingRedisService reservationPendingRedisService;

    @PostMapping
    public ResponseEntity<ReservationPendingResponse> pendingReservation(
            @CurrentUser Long userId, @Valid @RequestBody ReservationPendingRequest request) {
        String requestId = RequestNumberGenerator.generateRequestNumber(userId);

        reservationPendingProducer.sendToQueue(requestId, userId, request.getConcertId());
        reservationPendingRedisService.saveStatus(requestId, ReservationStatus.PENDING.name());

        ReservationPendingResponse response = new ReservationPendingResponse(
                requestId,
                "Reservation request added to the queue.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/request-id/{requestId}/details")
    public ResponseEntity<ReservationPendingDetailsResponse> getReservationDetails(@PathVariable String requestId) {
        String status = reservationPendingRedisService.getStatusByRequestId(requestId);
        if (status == null || status.equals(ReservationStatus.PENDING.name())) {
            return ResponseEntity.status(404).body(new ReservationPendingDetailsResponse(requestId, "[Error] Request ID not found.", null));
        }
        String reservationId = reservationPendingRedisService.getReservationIdByRequestId(requestId);

        return ResponseEntity.ok(new ReservationPendingDetailsResponse(requestId, status, reservationId));
    }

}
