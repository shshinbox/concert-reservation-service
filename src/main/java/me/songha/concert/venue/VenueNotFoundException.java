package me.songha.concert.venue;

import me.songha.concert.shared.exception.NotFoundException;

public class VenueNotFoundException extends NotFoundException {
    public VenueNotFoundException(String message) {
        super(message);
    }
}
