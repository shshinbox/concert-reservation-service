package me.songha.concert.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationPublisher {
    private static final String TOPIC_PREFIX = "concert-notifications:";
    private final RedisTemplate<String, Notification> notificationRedisTemplate;

    public void notifySubscribers(Notification notification) {
        String topic = TOPIC_PREFIX + notification.getChannelName();

        notificationRedisTemplate.convertAndSend(topic, notification);
    }
}