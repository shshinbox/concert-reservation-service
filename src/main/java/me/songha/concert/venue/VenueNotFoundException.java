package me.songha.concert.venue;

import me.songha.concert.common.exception.NotFoundException;

public class VenueNotFoundException extends NotFoundException {
    public VenueNotFoundException(String message) {
        super(message);
    }
}
