package me.songha.concert.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/notification")
@RequiredArgsConstructor
@RestController
public class NotificationController {
    private final NotificationPublisher notificationPublisher;

    @PostMapping
    public ResponseEntity<Void> notification(@RequestBody Notification notification) {
        notificationPublisher.notifySubscribers(notification);
        return ResponseEntity.status(201).build();
    }
}
