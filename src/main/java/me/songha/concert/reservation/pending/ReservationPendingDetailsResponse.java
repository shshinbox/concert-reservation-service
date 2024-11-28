package me.songha.concert.reservation.pending;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReservationPendingDetailsResponse {
    private String requestId;
    private String status;
    private String reservationId;
}
