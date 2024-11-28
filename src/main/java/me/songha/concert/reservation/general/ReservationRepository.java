package me.songha.concert.reservation.general;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
            select r
            from Reservation r
            join fetch r.concert c
            where r.userId = :userId
            """)
    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    @Query("""
            select r
            from Reservation r
            join fetch r.concert c
            where r.concert.id = :concertId
            """)
    Page<Reservation> findByConcertId(Long concertId, Pageable pageable);

    @Query(value = """
            select r
            from Reservation r
            join fetch r.concert c
            where r.id = :id order by r.id desc limit 1
            """)
    Optional<Reservation> findReservationById(Long id);

    @Query(value = """
            select r
            from Reservation r
            join fetch r.concert c
            where r.userId = :userId
            and r.concert.id = :concertId
            order by r.id desc
            limit 1
            """)
    Optional<Reservation> findTopByUserIdAndConcertId(Long userId, Long concertId);
}
