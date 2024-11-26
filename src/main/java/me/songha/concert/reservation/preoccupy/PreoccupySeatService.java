package me.songha.concert.reservation.preoccupy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.reservation.pending.ReservationPendingRedisService;
import me.songha.concert.reservation.seat.ReservationSeatNotAvailableException;
import me.songha.concert.reservation.seat.ReservationSeatPreoccupyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PreoccupySeatService {
    private final ReservationSeatPreoccupyService reservationSeatPreoccupyService;
    private final ReservationPendingRedisService reservationPendingRedisService;

    public PreoccupySeatsResponse preoccupySeats(Long concertId, Long userId, String requestId, List<String> seatNumbers) {
        reservationPendingRedisService.validRequestId(requestId);

        for (String seatNumber : seatNumbers) {
            boolean isSeatAvailable = reservationSeatPreoccupyService.isSeatAvailable(concertId, seatNumber);
            if (!isSeatAvailable) {
                throw new ReservationSeatNotAvailableException("[Error] Seat is not available.");
            }
            boolean isPreoccupy = reservationSeatPreoccupyService.preoccupySeats(concertId, seatNumber, userId);
            if (!isPreoccupy) {
                throw new ReservationSeatNotAvailableException("[Error] Seat is not available.");
            }
        }

        return new PreoccupySeatsResponse(concertId, userId, seatNumbers, "Seats have been reserved.");
    }

}
