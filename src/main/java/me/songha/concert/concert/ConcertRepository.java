package me.songha.concert.concert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    @Query("""
            select c
            from Concert c
            join fetch c.venue v
            where c.id = :concertId
            """)
    Optional<Concert> findConcertWithVenueById(Long concertId);
}
