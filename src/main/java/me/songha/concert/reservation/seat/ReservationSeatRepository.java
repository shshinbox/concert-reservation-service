package me.songha.concert.reservation.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    @Query("""
            select new me.songha.concert.reservation.seat.ReservationSeatDto(rs.id, rs.reservation.id, s.seatNumber, rs.price)
            from ReservationSeat rs
            join rs.seat s
            where rs.reservation.id in :reservationIds
            """)
    List<ReservationSeatDto> findByReservationIds(List<Long> reservationIds);
}
