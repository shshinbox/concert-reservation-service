package me.songha.concert.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationSubscriber implements MessageListener {
    private final RedisTemplate<String, Notification> notificationRedisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channelName = new String(message.getChannel());
            Notification notification = (Notification) notificationRedisTemplate.getValueSerializer().deserialize(message.getBody());

            // todo
            System.out.println("알림 채널명: " + channelName);
            System.out.println("알림 수신: " + notification);

        } catch (Exception e) {
            log.error("[Error] Error during message subscription. e.message: {}", e.getMessage());
        }
    }
}