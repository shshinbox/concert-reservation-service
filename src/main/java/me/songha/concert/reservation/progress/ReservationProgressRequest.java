package me.songha.concert.reservation.progress;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReservationProgressRequest {
    @NotNull
    private Long reservationId;
    @NotNull
    private Long concertId;
    @NotNull
    private List<String> seatNumbers;
}
