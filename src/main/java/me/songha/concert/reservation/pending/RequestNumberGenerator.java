package me.songha.concert.reservation.pending;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class RequestNumberGenerator {
    public static String generateRequestNumber(Long userId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomDigits = String.format("%03d", ThreadLocalRandom.current().nextInt(1000));
        return "RES" + userId + "-" + timestamp + "-" + randomDigits;
    }
}
