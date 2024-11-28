package me.songha.concert.venue;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.songha.concert.shared.entity.BaseTimeEntity;
import me.songha.concert.seat.Seat;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
@Table(name = "VENUE")
@Entity
public class Venue extends BaseTimeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @Min(0)
    @NotNull
    private Integer capacity;

    @Min(1)
    @NotNull
    @OneToMany(mappedBy = "venue", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Seat> seats;

    @Builder
    public Venue(Long id, String name, Integer capacity, List<Seat> seats) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.seats = seats;
    }

    public void update(String name, Integer capacity, List<Seat> seats) {
        this.name = name;
        this.capacity = capacity;
        this.seats = seats;
    }
}
