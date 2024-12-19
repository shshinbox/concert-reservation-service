package me.songha.concert.reservation.pending;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.songha.concert.reservation.common.ReservationKafkaLagService;
import me.songha.concert.reservation.general.ReservationStatus;
import me.songha.concert.reservation.reservationevent.ReservationEventLogger;
import me.songha.concert.shared.security.CurrentUser;
import me.songha.concert.shared.util.RequestNumberGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RequestMapping("/reservation/pending")
@RequiredArgsConstructor
@RestController
public class ReservationPendingController {
    private final ReservationPendingProducer reservationPendingProducer;
    private final ReservationPendingRedisService reservationPendingRedisService;
    private final ReservationKafkaLagService reservationKafkaLagService;

    @ReservationEventLogger
    @PostMapping
    public ResponseEntity<ReservationPendingResponse> pendingReservation(
            @CurrentUser Long userId, @Valid @RequestBody ReservationPendingRequest request) throws ExecutionException, InterruptedException {
        String requestId = RequestNumberGenerator.generateRequestNumber(userId);

        long[] result = reservationPendingProducer.sendAndReturnMetadata(requestId, userId, request.getConcertId());
        reservationPendingRedisService.saveStatus(requestId, ReservationStatus.PENDING.name());

        ReservationPendingResponse response = new ReservationPendingResponse(
                requestId, "Reservation request added to the queue.", result[0], result[1]);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/request-id/{requestId}/details")
    public ResponseEntity<ReservationPendingDetailsResponse> getReservationDetails(
            @PathVariable String requestId, int partition, long userOffset) throws ExecutionException, InterruptedException {
        String status = reservationPendingRedisService.getStatusByRequestId(requestId);

        if (status == null || status.equals(ReservationStatus.PENDING.name())) {
            long lag = reservationKafkaLagService.getLag(partition, userOffset);
            ReservationPendingDetailsResponse response = new ReservationPendingDetailsResponse(
                    requestId, status, null, lag, "[Error] Request ID not found.");
            return ResponseEntity.status(404).body(response);
        }

        String reservationId = reservationPendingRedisService.getReservationIdByRequestId(requestId);
        ReservationPendingDetailsResponse response = new ReservationPendingDetailsResponse(
                requestId, status, reservationId, 0, "The reservation queue has been successfully processed.");

        return ResponseEntity.ok(response);
    }

}
