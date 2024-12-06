package me.songha.concert.reservation.seat;

import me.songha.concert.reservation.general.Reservation;
import me.songha.concert.seat.Seat;

public class ReservationSeatConverter {
    public static ReservationSeat toEntity(ReservationSeatDto reservationSeatDto, Seat seat, Reservation reservation) {
        return ReservationSeat.builder()
                .id(reservationSeatDto.getId())
                .price(reservationSeatDto.getPrice())
                .seat(seat)
                .reservation(reservation)
                .build();
    }

    public static ReservationSeatDto toDto(ReservationSeat reservationSeat) {
        return ReservationSeatDto.builder()
                .id(reservationSeat.getId())
                .price(reservationSeat.getPrice())
                .seatNumber(reservationSeat.getSeat().getSeatNumber())
                .reservationId(reservationSeat.getReservation().getId())
                .build();
    }
}
