package me.songha.concert.notification;

import lombok.Data;

@Data
public class Notification {
    private String title;
    private String message;
    private String channelName;
}
