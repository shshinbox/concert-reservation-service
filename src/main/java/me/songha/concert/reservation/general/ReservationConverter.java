package me.songha.concert.reservation.general;

import me.songha.concert.concert.Concert;
import me.songha.concert.reservation.seat.ReservationSeat;

import java.util.List;

public class ReservationConverter {
    public static Reservation toEntity(ReservationDto reservationDto, Concert concert, List<ReservationSeat> reservationSeats) {
        return Reservation.builder()
                .id(reservationDto.getId())
                .userId(reservationDto.getUserId())
                .concert(concert)
                .totalAmount(reservationDto.getTotalAmount())
                .status(ReservationStatus.fromString(reservationDto.getReservationStatus()))
                .reservationSeats(reservationSeats)
                .build();
    }

    public static ReservationDto toDto(Reservation reservation) {
        return ReservationDto.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .concertId(reservation.getConcert().getId())
                .concertTitle(reservation.getConcert().getTitle())
                .reservationStatus(reservation.getStatus().name())
                .totalAmount(reservation.getTotalAmount())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }

}
