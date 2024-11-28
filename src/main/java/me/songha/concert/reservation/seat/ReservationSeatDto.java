package me.songha.concert.reservation.seat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReservationSeatDto {
    private Long id;
    private Long reservationId;
    private String seatNumber;
    private Integer price;
}
