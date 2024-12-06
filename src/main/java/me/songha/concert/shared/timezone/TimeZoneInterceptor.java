package me.songha.concert.shared.timezone;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.ZoneId;

@Component
public class TimeZoneInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientTimeZone = request.getHeader("Time-Zone");
        if (clientTimeZone != null) {
            TimeZoneContextHolder.setTimeZone(ZoneId.of(clientTimeZone));
        } else {
            TimeZoneContextHolder.setTimeZone(ZoneId.of("UTC"));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TimeZoneContextHolder.clear();
    }
}