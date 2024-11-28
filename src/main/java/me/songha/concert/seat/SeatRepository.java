package me.songha.concert.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("select vs from Seat vs where vs.venue.id = :venueId")
    List<Seat> findByVenueId(Long venueId);

    @Query("select s.grade from Seat s where s.seatNumber = :seatNumber")
    Optional<String> findGradeBySeatNumber(String seatNumber);

    @Query("""
            select s.seatNumber
            from Seat s
            join s.reservationSeats rs
            join rs.reservation r
            where r.concert.id = :concertId
            """)
    List<String> findSeatNumbersByConcertId(Long concertId);
}
