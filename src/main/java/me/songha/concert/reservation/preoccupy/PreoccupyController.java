package me.songha.concert.reservation.preoccupy;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.songha.concert.common.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/reservation/preoccupy")
@RequiredArgsConstructor
@RestController
public class PreoccupyController {
    private final PreoccupySeatService preoccupySeatService;

    @PostMapping
    public ResponseEntity<PreoccupySeatsResponse> preoccupySeats(@CurrentUser Long userId, @Valid @RequestBody PreoccupySeatRequest request) {
        PreoccupySeatsResponse response = preoccupySeatService.preoccupySeats(
                request.getConcertId(), userId, request.getRequestId(), request.getSeatNumbers());
        return ResponseEntity.ok(response);
    }

}
