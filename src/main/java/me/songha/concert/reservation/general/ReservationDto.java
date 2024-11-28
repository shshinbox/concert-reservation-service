package me.songha.concert.reservation.general;

import lombok.Builder;
import lombok.Data;
import me.songha.concert.reservation.seat.ReservationSeatDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationDto {
    private Long id;
    private Long userId;
    private Long concertId;
    private String concertTitle;
    private Integer totalAmount;
    private String reservationStatus;
    private List<ReservationSeatDto> reservationSeats;
    private String reservationNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ReservationDto(Long id, Long userId, Long concertId, String concertTitle, Integer totalAmount,
                          String reservationStatus, List<ReservationSeatDto> reservationSeats, String reservationNumber,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.concertId = concertId;
        this.concertTitle = concertTitle;
        this.totalAmount = totalAmount;
        this.reservationStatus = reservationStatus;
        this.reservationSeats = reservationSeats;
        this.reservationNumber = reservationNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
