package me.songha.concert.reservation.seat;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.songha.concert.common.entity.BaseTimeEntity;
import me.songha.concert.reservation.general.Reservation;
import me.songha.concert.seat.Seat;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
@Table(name = "RESERVATION_SEAT")
@Entity
public class ReservationSeat extends BaseTimeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_seat_id")
    private Seat seat;

    @Min(0)
    private Integer price;

    @Builder
    public ReservationSeat(Long id, Reservation reservation, Seat seat, Integer price) {
        this.id = id;
        this.reservation = reservation;
        this.seat = seat;
        this.price = price;
    }
}
