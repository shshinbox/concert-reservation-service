package me.songha.concert.concert;

import me.songha.concert.common.exception.NotFoundException;

public class ConcertNotFoundException extends NotFoundException {
    public ConcertNotFoundException(String message) {
        super(message);
    }
}
