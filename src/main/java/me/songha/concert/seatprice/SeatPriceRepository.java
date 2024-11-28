package me.songha.concert.seatprice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatPriceRepository extends JpaRepository<SeatPrice, Long> {
    @Query("""
            select cp
            from SeatPrice cp
            join cp.concert c
            where c.id = :concertId
            """)
    List<SeatPrice> findByConcertId(Long concertId);

    @Query("""
            select sum(sp.price)
            from SeatPrice sp
            join sp.concert c
            where c.id = :concertId
            and sp.grade = :grade
            """)
    Optional<Integer> findAmountByConcertIdAndGrade(Long concertId, SeatGrade grade);
}
