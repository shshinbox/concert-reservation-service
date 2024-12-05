package me.songha.concert.reservation.reservationevent;

import lombok.Data;
import me.songha.concert.reservation.general.ReservationStatus;
import me.songha.concert.shared.exception.ReservationIllegalArgumentException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "reservation_history")
public class ReservationEvent {
    @Id
    private String id;
    private Object[] parameters;
    private Map<String, Object> body;
    private String status;
    private LocalDateTime timestamp;

    public ReservationEvent(Object[] parameters, Map<String, Object> body, String methodName, String status) {
        this.parameters = parameters;
        this.body = body;
        this.timestamp = LocalDateTime.now();
        setStatus(methodName, status);
    }

    private void setStatus(String methodName, String status) {
        switch (methodName) {
            case "pendingReservation" -> this.status = ReservationStatus.PENDING.name();
            case "preoccupySeats" -> this.status = ReservationStatus.PROCESSING.name();
            case "progressReservation" -> {
                switch (status) {
                    case "CONFIRMED" -> this.status = ReservationStatus.CONFIRMED.name();
                    case "REJECTED" -> this.status = ReservationStatus.REJECTED.name();
                    case "CANCELED" -> this.status = ReservationStatus.CANCELED.name();
                }
            }
            default -> throw new ReservationIllegalArgumentException("[Error] Unknown method name: " + methodName);
        }
    }

}