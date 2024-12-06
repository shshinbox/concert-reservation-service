package me.songha.concert.shared.timezone;

import java.time.ZoneId;

public class TimeZoneContextHolder {
    private static final ThreadLocal<ZoneId> timeZoneHolder = new ThreadLocal<>();

    public static void setTimeZone(ZoneId zoneId) {
        timeZoneHolder.set(zoneId);
    }

    public static ZoneId getZoneId() {
        return timeZoneHolder.get() != null ? timeZoneHolder.get() : ZoneId.of("UTC");
    }

    public static void clear() {
        timeZoneHolder.remove();
    }
}