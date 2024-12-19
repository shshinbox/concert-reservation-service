package me.songha.concert.reservation.common;

import lombok.RequiredArgsConstructor;
import me.songha.concert.shared.messaging.KafkaLagService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Service
public class ReservationKafkaLagService {
    private final KafkaLagService kafkaLagService;

    public long getLag(int partition, long userOffset) throws ExecutionException, InterruptedException {
        return kafkaLagService.getLag(ReservationProperties.RESERVATION_GROUP_ID, ReservationProperties.RESERVATION_TOPIC, partition, userOffset);
    }

}
