package me.songha.concert.shared.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.shared.mongo.reservationevent.ReservationEvent;
import me.songha.concert.shared.mongo.reservationevent.ReservationEventRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class ReservationEventAspect extends AbstractCommonAspect {
    private final ReservationEventRepository reservationEventRepository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(me.songha.concert.shared.aspect.ReservationEventLogger)")
    public Object logReservationHistory(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = getMethod(joinPoint).getName();
        Object[] args = getParameter(joinPoint);
        String parameters;

        try {
            parameters = objectMapper.writeValueAsString(args);
        } catch (Exception e) {
            parameters = "Error to parse parameters.";
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error(">>[Error] method: {}, Throwable message: {}", methodName, throwable.getMessage());
            throw throwable;
        }

        String resultString;
        try {
            resultString = result != null ? objectMapper.writeValueAsString(result) : "null";
        } catch (Exception e) {
            resultString = "Error to parse parameters.";
        }

        try {
            ReservationEvent history = new ReservationEvent();
            history.setMethodName(methodName);
            history.setParameters(parameters);
            history.setResult(resultString);
            history.setTimestamp(LocalDateTime.now());
            reservationEventRepository.save(history);
        } catch (Exception e) {
            log.error("[Exception] method: {}, exception class: {}, exception message: {}", methodName, e.getClass(), e.getMessage());
        }

        return result;
    }

}
