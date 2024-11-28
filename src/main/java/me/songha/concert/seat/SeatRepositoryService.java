package me.songha.concert.seat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SeatRepositoryService {
    private final SeatRepository seatRepository;

    public List<String> getSoldSeatsByConcert(Long concertId) {
        return seatRepository.findSeatNumbersByConcertId(concertId);
    }

    public List<Seat> getSeatsBySeatNumbers(List<String> seatNumbers) {
        return seatRepository.findSeatsBySeatNumber(seatNumbers);
    }

}
