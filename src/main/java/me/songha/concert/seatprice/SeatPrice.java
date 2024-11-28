package me.songha.concert.seatprice;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.songha.concert.shared.entity.BaseTimeEntity;
import me.songha.concert.concert.Concert;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
@Table(name = "SEAT_PRICE")
@Entity
public class SeatPrice extends BaseTimeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SeatGrade grade;

    @Min(0)
    @NotNull
    private Integer price;
}