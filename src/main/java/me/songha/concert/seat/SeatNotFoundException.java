package me.songha.concert.seat;

import me.songha.concert.common.exception.NotFoundException;

public class SeatNotFoundException extends NotFoundException {
    public SeatNotFoundException(String message) {
        super(message);
    }
}
