package me.songha.concert.seat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.songha.concert.common.entity.BaseTimeEntity;
import me.songha.concert.reservation.seat.ReservationSeat;
import me.songha.concert.seatprice.SeatGrade;
import me.songha.concert.venue.Venue;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
@Table(name = "SEAT")
@Entity
public class Seat extends BaseTimeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull
    private String seatNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SeatGrade grade;

    @OneToMany(mappedBy = "seat", fetch = FetchType.LAZY)
    private List<ReservationSeat> reservationSeats;

    @Builder
    public Seat(Long id, String seatNumber, Venue venue, SeatGrade grade, List<ReservationSeat> reservationSeats) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.venue = venue;
        this.grade = grade;
        this.reservationSeats = reservationSeats;
    }
}
