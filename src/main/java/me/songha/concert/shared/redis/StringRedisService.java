package me.songha.concert.shared.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class StringRedisService {
    private final StringRedisTemplate redisTemplate;

    public Boolean create(String key, String value) {
        return create(key, value, 10);
    }

    public Boolean create(String key, String value, int expired) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, expired, TimeUnit.MINUTES);
    }

    public void update(String key, String value) {
        update(key, value, 10);
    }

    public void update(String key, String value, int expired) {
        redisTemplate.opsForValue().set(key, value, expired, TimeUnit.MINUTES);
    }

    public Set<String> getKeysByPattern(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
