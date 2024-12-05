package me.songha.concert.reservation.reservationevent;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.shared.mongo.AbstractMongoService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ReservationEventMongoService extends AbstractMongoService<ReservationEvent> {
    public ReservationEventMongoService(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        super(mongoTemplate, objectMapper);
    }

    public void saveReservationEvent(Object[] parameters, String methodName, Object result) throws Exception {
        ReservationEvent reservationEvent = mapToObject(parameters, methodName, result);
        save(reservationEvent);
    }

    @Override
    protected ReservationEvent mapToObject(Object[] parameters, String methodName, Object result) throws Exception {
        String jsonString = objectMapper.writeValueAsString(result);
        Map<String, Object> data = objectMapper.readValue(jsonString, Map.class);
        Map<String, Object> body = (Map<String, Object>) data.get("body");
        String status = (String) body.get("status");
        return new ReservationEvent(parameters, body, methodName, status);
    }

    @Override
    protected ReservationEvent mapToObject(String jsonResponse) throws Exception {
        throw new UnsupportedOperationException("[Error] Not supported.");
    }
}
