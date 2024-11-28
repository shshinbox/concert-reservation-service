package me.songha.concert.reservation.history;

import me.songha.concert.shared.exception.NotFoundException;

public class ReservationHistoryNotFoundException extends NotFoundException {
    public ReservationHistoryNotFoundException(String message) {
        super(message);
    }
}
