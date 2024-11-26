package me.songha.concert.payment;

import lombok.RequiredArgsConstructor;
import me.songha.concert.seat.SeatRepositoryService;
import me.songha.concert.seatprice.SeatGrade;
import me.songha.concert.seatprice.SeatPriceRepositoryService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class TotalAmountService {
    private final SeatPriceRepositoryService seatPriceRepositoryService;
    private final SeatRepositoryService seatRepositoryService;

    public int getTotalAmount(Long concertId, List<String> seatNumbers) {
        Map<SeatGrade, Integer> tickets = getGroupingTickets(seatNumbers);
        return seatPriceRepositoryService.getTotalAmount(concertId, tickets);
    }

    private Map<SeatGrade, Integer> getGroupingTickets(List<String> seatNumbers) {
        Map<SeatGrade, Integer> result = new HashMap<>();
        seatNumbers.forEach(seatNumber -> {
            SeatGrade grade = seatRepositoryService.getGradeBySeatNumber(seatNumber);
            result.put(grade, result.getOrDefault(grade, 0) + 1);
        });
        return result;
    }
}
