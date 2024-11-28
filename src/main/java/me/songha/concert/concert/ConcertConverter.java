package me.songha.concert.concert;

import me.songha.concert.venue.Venue;
import org.springframework.stereotype.Component;

@Component
public class ConcertConverter {
    public Concert toEntity(ConcertDto dto, Venue venue) {
        return Concert.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .venue(venue)
                .concertDate(dto.getConcertDate())
                .runningTime(dto.getRunningTime())
                .salesStartAt(dto.getSalesStartAt())
                .salesEndAt(dto.getSalesEndAt())
                .build();
    }

    public ConcertDto toDto(Concert concert) {
        return ConcertDto.builder()
                .id(concert.getId())
                .title(concert.getTitle())
                .description(concert.getDescription())
                .concertDate(concert.getConcertDate())
                .runningTime(concert.getRunningTime())
                .salesStartAt(concert.getSalesStartAt())
                .salesEndAt(concert.getSalesEndAt())
                .createdAt(concert.getCreatedAt())
                .updatedAt(concert.getUpdatedAt())
                .build();
    }
}