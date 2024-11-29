package me.songha.concert.shared.mongo.reservationevent;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "reservation_history")
public class ReservationEvent {
    @Id
    private String id;
    private String methodName;
    private String parameters;
    private String result;
    private LocalDateTime timestamp;
}