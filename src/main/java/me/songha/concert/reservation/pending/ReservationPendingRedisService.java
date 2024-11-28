package me.songha.concert.reservation.pending;

import lombok.RequiredArgsConstructor;
import me.songha.concert.shared.redis.StringRedisService;
import me.songha.concert.shared.exception.ReservationIllegalArgumentException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationPendingRedisService {
    private final StringRedisService redisService;
    private static final String RESERVATION_STATUS_KEY_PREFIX = "reservation-status:";
    private static final String RESERVATION_ID_KEY_PREFIX = "reservation-id:";

    public void saveStatus(String requestId, String status) {
        String key = RESERVATION_STATUS_KEY_PREFIX + requestId;
        redisService.create(key, status);
    }

    public void updateStatus(String requestId, String status) {
        String key = RESERVATION_STATUS_KEY_PREFIX + requestId;
        redisService.update(key, status);
    }

    public String getStatusByRequestId(String requestId) {
        String key = RESERVATION_STATUS_KEY_PREFIX + requestId;
        return redisService.getValue(key);
    }

    public String getReservationIdByRequestId(String requestId) {
        String key = RESERVATION_ID_KEY_PREFIX + requestId;
        return redisService.getValue(key);
    }

    public Boolean saveReservationIdByRequestId(String requestId, Long reservationId) {
        String key = RESERVATION_ID_KEY_PREFIX + requestId;
        return redisService.create(key, String.valueOf(reservationId));
    }

    public void validRequestId(String requestId) {
        String reservationId = getReservationIdByRequestId(requestId);
        if (reservationId == null) {
            throw new ReservationIllegalArgumentException("Invalid requestId: " + requestId);
        }
    }
}