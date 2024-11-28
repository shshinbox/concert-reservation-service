package me.songha.concert.reservation.preoccupy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.reservation.pending.ReservationPendingRedisService;
import me.songha.concert.reservation.seat.ReservationSeatNotAvailableException;
import me.songha.concert.seat.SeatRepositoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PreoccupySeatService {
    private final PreoccupyRedisService preoccupyRedisService;
    private final ReservationPendingRedisService reservationPendingRedisService;
    private final SeatRepositoryService seatRepositoryService;

    public PreoccupySeatsResponse preoccupySeats(Long concertId, Long userId, String requestId, List<String> seatNumbers) {
        reservationPendingRedisService.validRequestId(requestId);

        List<String> soldSeats = seatRepositoryService.getSoldSeatsByConcert(concertId);
        boolean isSold = seatNumbers.stream().anyMatch(soldSeats::contains);
        if (isSold) {
            throw new ReservationSeatNotAvailableException("[Error] Seat is not available.");
        }

        for (String seatNumber : seatNumbers) {
            boolean isSeatAvailable = preoccupyRedisService.isSeatAvailable(concertId, seatNumber);
            if (!isSeatAvailable) {
                throw new ReservationSeatNotAvailableException("[Error] Seat is not available.");
            }
            boolean isPreoccupy = preoccupyRedisService.preoccupySeats(concertId, seatNumber, userId);
            if (!isPreoccupy) {
                throw new ReservationSeatNotAvailableException("[Error] Seat is not available.");
            }
        }

        return new PreoccupySeatsResponse(concertId, userId, seatNumbers, "Seats have been reserved.");
    }

}
