package me.songha.concert.venue;

import me.songha.concert.seat.Seat;

import java.util.List;

public class VenueConverter {
    public static Venue toEntity(VenueDto venueDto, List<Seat> seats) {
        return Venue.builder()
                .id(venueDto.getId())
                .capacity(venueDto.getCapacity())
                .seats(seats)
                .name(venueDto.getName())
                .build();
    }

    public static VenueDto toDto(Venue venue) {
        return VenueDto.builder()
                .id(venue.getId())
                .capacity(venue.getCapacity())
                .name(venue.getName())
                .build();
    }
}
