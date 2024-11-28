package me.songha.concert.seat;

import me.songha.concert.shared.exception.NotFoundException;

public class SeatNotFoundException extends NotFoundException {
    public SeatNotFoundException(String message) {
        super(message);
    }
}
