package me.songha.concert.seat;

import lombok.RequiredArgsConstructor;
import me.songha.concert.seatprice.SeatGrade;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SeatRepositoryService {
    private final SeatRepository seatRepository;

    public List<String> getSoldSeatsByConcert(Long concertId) {
        return seatRepository.findSeatNumbersByConcertId(concertId);
    }

    public SeatGrade getGradeBySeatNumber(String seatNumber) {
        String grade = seatRepository.findGradeBySeatNumber(seatNumber)
                .orElseThrow(() -> new SeatNotFoundException("[Error] Seat not found."));
        return SeatGrade.fromString(grade);
    }

}
