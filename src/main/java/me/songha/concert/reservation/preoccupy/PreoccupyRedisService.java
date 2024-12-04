package me.songha.concert.reservation.preoccupy;

import lombok.RequiredArgsConstructor;
import me.songha.concert.shared.redis.StringRedisService;
import me.songha.concert.shared.exception.ReservationIllegalArgumentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional
@RequiredArgsConstructor
@Service
public class PreoccupyRedisService {
    private static final String RESERVATION_SEAT_KEY_PREFIX = "concert:reservation_seat_concert:%d:seat:%s";
    private static final String RESERVATION_SEAT_KEY_PATTERN_PREFIX = "concert:reservation_seat_concert:%d:seat:*";
    private final StringRedisService redisService;

    public boolean preoccupySeats(Long concertId, String seatNumber, Long userId) {
        String key = String.format(RESERVATION_SEAT_KEY_PREFIX, concertId, seatNumber);
        return redisService.create(key, String.valueOf(userId));
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
        Set<String> preoccupiedSeats = getPreoccupiedSeatsByConcert(concertId);

        return !preoccupiedSeats.contains(seatKey);
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
