package me.songha.concert.shared.util;

import me.songha.concert.shared.exception.ReservationIllegalArgumentException;

import java.util.concurrent.ThreadLocalRandom;

public class RandomNumberGenerator {
    public static String generateCertificationCode(int length) {
        if (length <= 0) {
            throw new ReservationIllegalArgumentException("[Error] Length must be at least 1.");
        }

        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;

        return String.valueOf(ThreadLocalRandom.current().nextInt(min, max + 1));
    }
}