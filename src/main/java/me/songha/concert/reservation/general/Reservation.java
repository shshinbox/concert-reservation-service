package me.songha.concert.reservation.general;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import me.songha.concert.common.entity.BaseTimeEntity;
import me.songha.concert.concert.Concert;
import me.songha.concert.reservation.seat.ReservationSeat;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
@Table(name = "RESERVATION")
@Entity
public class Reservation extends BaseTimeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @Min(0)
    private Integer totalAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Size(max = 4)
    @NotNull
    @OneToMany(mappedBy = "reservation", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private List<ReservationSeat> reservationSeats;

    @Pattern(regexp = "^[0-9]{14}-[0-9]{4}-[0-9]{3}$")
    private String reservationNumber;

    @Builder
    public Reservation(Long id, Long userId, Concert concert, Integer totalAmount,
                       ReservationStatus status, List<ReservationSeat> reservationSeats, String reservationNumber) {
        this.id = id;
        this.userId = userId;
        this.concert = concert;
        this.totalAmount = totalAmount;
        this.status = status;
        this.reservationSeats = reservationSeats;
        this.reservationNumber = reservationNumber;
    }

    public void update(Integer totalAmount, ReservationStatus status, String reservationNumber) {
        this.totalAmount = totalAmount;
        this.status = status;
        this.reservationNumber = reservationNumber;
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }
}
