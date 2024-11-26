package me.songha.concert.seatprice;

import me.songha.concert.common.exception.ReservationIllegalArgumentException;

import java.util.Arrays;
import java.util.List;

public enum SeatGrade {
    VIP, R, S, A, B;

    public static SeatGrade fromString(String status) {
        return switch (status) {
            case "VIP" -> VIP;
            case "R" -> R;
            case "S" -> S;
            case "A" -> A;
            case "B" -> B;
            default -> throw new ReservationIllegalArgumentException("[Error] Invalid reservation status: " + status);
        };
    }

    public static List<SeatGrade> toList() {
        return Arrays.asList(SeatGrade.values());
    }

    public static List<String> toStringList() {
        return Arrays.stream(SeatGrade.values())
                .map(Enum::name)
                .toList();
    }
}
