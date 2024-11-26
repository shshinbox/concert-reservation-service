package me.songha.concert.reservation.general;

import me.songha.concert.common.exception.NotFoundException;

public class ReservationNotFoundException extends NotFoundException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
