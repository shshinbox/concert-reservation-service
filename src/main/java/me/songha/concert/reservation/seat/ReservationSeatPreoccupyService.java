package me.songha.concert.reservation.seat;

import lombok.RequiredArgsConstructor;
import me.songha.concert.common.exception.ReservationIllegalArgumentException;
import me.songha.concert.common.StringRedisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional
@RequiredArgsConstructor
@Service
public class ReservationSeatPreoccupyService {
    private static final String RESERVATION_SEAT_KEY_PREFIX = "reservation_seat_concert:%d:seat:%s";
    private static final String RESERVATION_SEAT_KEY_PATTERN_PREFIX = "reservation_seat_concert:%d:seat:*";
    private final StringRedisService redisService;
    private final ReservationSeatRepository reservationSeatRepository;

    public boolean preoccupySeats(Long concertId, String seatNumber, Long userId) {
        String key = String.format(RESERVATION_SEAT_KEY_PREFIX, concertId, seatNumber);
        return redisService.create(key, String.valueOf(userId));
    }

    public List<String> getSoldSeatsByConcert(Long concertId) {
        return reservationSeatRepository.findSeatNumbersByConcertId(concertId);
    }

    public Set<String> getPreoccupiedSeatsByConcert(Long concertId) {
        String pattern = String.format(RESERVATION_SEAT_KEY_PATTERN_PREFIX, concertId);
        return redisService.getKeysByPattern(pattern);
    }

    public String getUserIdByConcertAndSeatNumber(Long concertId, String seatNumber) {
        String key = String.format(RESERVATION_SEAT_KEY_PREFIX, concertId, seatNumber);
        return redisService.getValue(key);
    }

    public boolean isSeatAvailable(Long concertId, String seatNumber) {
        String seatKey = String.format(RESERVATION_SEAT_KEY_PREFIX, concertId, seatNumber);
        List<String> soldSeats = getSoldSeatsByConcert(concertId);
        Set<String> preoccupiedSeats = getPreoccupiedSeatsByConcert(concertId);

        boolean isSold = soldSeats.contains(seatNumber);
        boolean isReserved = preoccupiedSeats.contains(seatKey);

        return !(isSold || isReserved);
    }

    public void isSeatValidatedForCurrentUser(Long concertId, Long userId, List<String> seatNumbers) {
        for (String seatNumber : seatNumbers) {
            boolean isValidatedSeatForCurrentUser = String.valueOf(userId).equals(getUserIdByConcertAndSeatNumber(concertId, seatNumber));
            if (!isValidatedSeatForCurrentUser) {
                throw new ReservationIllegalArgumentException("[Error] Seat is not validated for current user.");
            }
        }
    }

}
