package me.songha.concert.reservation.reservationevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.shared.aspect.AbstractCommonAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class ReservationEventAspect extends AbstractCommonAspect {
    private final ReservationEventMongoService reservationEventMongoService;

    @Around("@annotation(me.songha.concert.reservation.reservationevent.ReservationEventLogger)")
    public Object logReservationHistory(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = getMethod(joinPoint).getName();
        Object[] parameters = getParameters(joinPoint);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error(">>[Error] method: {}, Throwable message: {}", methodName, throwable.getMessage());
            throw throwable;
        }

        try {
            reservationEventMongoService.saveReservationEvent(parameters, methodName, result);
        } catch (Exception e) {
            log.error("[Exception] method: {}, exception class: {}, exception message: {}", methodName, e.getClass(), e.getMessage());
        }

        return result;
    }

}
