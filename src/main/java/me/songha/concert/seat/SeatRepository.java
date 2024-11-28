package me.songha.concert.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("""
            select s
            from Seat s
            join fetch s.venue v
            where v.id = :venueId
            """)
    List<Seat> findByVenueId(Long venueId);

    @Query("""
            select s
            from Seat s
            where s.seatNumber in :seatNumbers
            """)
    List<Seat> findSeatsBySeatNumber(List<String> seatNumbers);

    @Query("""
            select s.seatNumber
            from Seat s
            join s.reservationSeats rs
            join rs.reservation r
            where r.concert.id = :concertId
            """)
    List<String> findSeatNumbersByConcertId(Long concertId);
}
