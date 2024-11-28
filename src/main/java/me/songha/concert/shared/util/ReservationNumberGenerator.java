package me.songha.concert.shared.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class ReservationNumberGenerator {
    public static String generateReservationNumber(Long reservationId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String reservationIdSuffix = String.format("%04d", reservationId % 10000);
        String randomDigits = String.format("%03d", ThreadLocalRandom.current().nextInt(1000));

        return timestamp + "-" + reservationIdSuffix + "-" + randomDigits;
    }
}
