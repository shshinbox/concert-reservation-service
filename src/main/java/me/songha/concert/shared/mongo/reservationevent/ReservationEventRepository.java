package me.songha.concert.shared.mongo.reservationevent;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationEventRepository extends MongoRepository<ReservationEvent, String> {
}