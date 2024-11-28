package me.songha.concert.concert;

import me.songha.concert.shared.exception.NotFoundException;

public class ConcertNotFoundException extends NotFoundException {
    public ConcertNotFoundException(String message) {
        super(message);
    }
}
