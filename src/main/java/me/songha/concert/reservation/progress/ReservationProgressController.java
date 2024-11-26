package me.songha.concert.reservation.progress;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.common.CurrentUser;
import me.songha.concert.reservation.general.ReservationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/reservation/progress")
@RequiredArgsConstructor
@RestController
public class ReservationProgressController {
    private final ReservationProgressService reservationProgressService;

    @PatchMapping
    public ResponseEntity<ReservationProgressResponse> progressReservation(
            @CurrentUser Long userId, @Valid @RequestBody ReservationProgressRequest request) {
        ReservationStatus status = reservationProgressService.progressReservation(request.getReservationId(), userId, request.getSeatNumbers(), request.getConcertId());
        ReservationProgressResponse response = new ReservationProgressResponse(
                request.getSeatNumbers(),
                status,
                "Reservation process has been completed.");
        return ResponseEntity.ok(response);
    }

}
