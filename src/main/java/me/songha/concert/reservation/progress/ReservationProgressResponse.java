package me.songha.concert.reservation.progress;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.songha.concert.reservation.general.ReservationStatus;

import java.util.List;

@Data
@AllArgsConstructor
public class ReservationProgressResponse {
    private List<String> seatNumbers;
    private ReservationStatus status;
    private String message;
}