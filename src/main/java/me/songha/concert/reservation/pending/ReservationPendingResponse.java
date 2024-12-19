package me.songha.concert.reservation.pending;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReservationPendingResponse {
    private String requestId;
    private String message;
    private long partition;
    private long offset;
}
