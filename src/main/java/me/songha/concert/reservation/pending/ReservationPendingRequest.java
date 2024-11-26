package me.songha.concert.reservation.pending;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationPendingRequest {
    @NotNull
    private Long concertId;
}