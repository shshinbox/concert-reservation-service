package me.songha.concert.seat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.songha.concert.common.entity.BaseTimeEntity;
import me.songha.concert.seatprice.SeatGrade;
import me.songha.concert.venue.Venue;

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

    @Builder
    public Seat(Long id, String seatNumber, Venue venue, SeatGrade grade) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.venue = venue;
        this.grade = grade;
    }
}
