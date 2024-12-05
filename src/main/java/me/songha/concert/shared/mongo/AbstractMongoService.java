package me.songha.concert.shared.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.mongodb.core.MongoTemplate;

public abstract class AbstractMongoService<T> {
    protected final MongoTemplate mongoTemplate;
    protected final ObjectMapper objectMapper;

    public AbstractMongoService(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    protected abstract T mapToObject(String jsonResponse) throws Exception;

    protected abstract T mapToObject(Object[] parameters, String methodName, Object result) throws Exception;

    protected void save(T obj, String collectionName) {
        mongoTemplate.save(obj, collectionName);
    }

    protected void save(T obj) {
        mongoTemplate.save(obj);
    }

}