package me.songha.concert.seatprice;

import me.songha.concert.shared.exception.NotFoundException;

public class SeatPriceNotFoundException extends NotFoundException {
    public SeatPriceNotFoundException(String message) {
        super(message);
    }
}
