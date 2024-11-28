package me.songha.concert.reservation.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    @Query("""
            select rs
            from ReservationSeat rs
            join fetch rs.seat s
            where rs.reservation.id in :reservationIds
            """)
    List<ReservationSeat> findByReservationIds(List<Long> reservationIds);
}
