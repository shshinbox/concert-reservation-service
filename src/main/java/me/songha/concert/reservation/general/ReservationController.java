package me.songha.concert.reservation.general;

import lombok.RequiredArgsConstructor;
import me.songha.concert.shared.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/reservation")
@RequiredArgsConstructor
@RestController
public class ReservationController {
    private final ReservationRepositoryService reservationRepositoryService;

    @GetMapping("/my")
    public ResponseEntity<Page<ReservationDto>> getReservationsByCurrentUser(@CurrentUser Long userId,
                                                                             @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<ReservationDto> reservations = reservationRepositoryService.getReservationsByUserId(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/my/concert-id/{concertId}")
    public ResponseEntity<ReservationDto> getReservationByCurrentUserAndConcertId(@CurrentUser Long userId, @PathVariable Long concertId) {
        ReservationDto reservationDto = reservationRepositoryService.getReservationByUserIdAndConcertId(userId, concertId);
        return ResponseEntity.ok(reservationDto);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ReservationDto> getReservation(@PathVariable Long id) {
        ReservationDto reservationDto = reservationRepositoryService.getReservation(id);
        return ResponseEntity.ok(reservationDto);
    }
}
