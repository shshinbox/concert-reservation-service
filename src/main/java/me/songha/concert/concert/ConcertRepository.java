package me.songha.concert.concert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    @Query("""
            select c
            from Concert c
            join c.venue v
            where c.id = :concertId
            """)
    Optional<Concert> findByConcertId(Long concertId);
}
