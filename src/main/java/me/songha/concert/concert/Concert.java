package me.songha.concert.concert;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.songha.concert.seatprice.SeatPrice;
import me.songha.concert.shared.entity.BaseTimeEntity;
import me.songha.concert.venue.Venue;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
@Table(name = "CONCERT")
@Entity
public class Concert extends BaseTimeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull
    private String title;

    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @NotNull
    @OneToMany(mappedBy = "concert", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<SeatPrice> seatPrices;

    @NotNull
    private LocalDateTime concertDate;

    @Min(1)
    @NotNull
    private Integer runningTime;

    @NotNull
    private LocalDateTime salesStartAt;

    @NotNull
    private LocalDateTime salesEndAt;

    @Builder
    public Concert(Long id, String title, String description, Venue venue, List<SeatPrice> seatPrices,
                   LocalDateTime concertDate, Integer runningTime, LocalDateTime salesStartAt, LocalDateTime salesEndAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.seatPrices = seatPrices;
        this.concertDate = concertDate;
        this.runningTime = runningTime;
        this.salesStartAt = salesStartAt;
        this.salesEndAt = salesEndAt;
    }

    public void update(String title, String description, Venue venue,
                       LocalDateTime concertDate, int runningTime) {
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.concertDate = concertDate;
        this.runningTime = runningTime;
    }
}
