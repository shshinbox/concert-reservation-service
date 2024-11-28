package me.songha.concert.reservation.seat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ReservationSeatDto {
    private Long id;
    private Long reservationId;
    private String seatNumber;
    private Integer price;

    @Builder
    public ReservationSeatDto(Long id, Long reservationId, String seatNumber, Integer price) {
        this.id = id;
        this.reservationId = reservationId;
        this.seatNumber = seatNumber;
        this.price = price;
    }
}
