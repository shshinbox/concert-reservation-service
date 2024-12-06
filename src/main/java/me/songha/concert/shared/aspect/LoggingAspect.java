package me.songha.concert.shared.aspect;

import lombok.extern.slf4j.Slf4j;
import me.songha.concert.shared.exception.NotFoundException;
import me.songha.concert.shared.exception.ReservationIllegalArgumentException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class LoggingAspect extends AbstractCommonAspect {

    @Before("reservationPackage() && allRestControllers()")
    public void logBefore(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        log.info("[Start] executing method: {}", method);

        Object[] args = getParameters(joinPoint);
        for (Object arg : args) {
            log.info(">>[Input] parameter type: {}, value: {}", arg.getClass().getSimpleName(), arg);
        }
    }

    @AfterReturning(pointcut = "reservationPackage() && allRestControllers()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        Method method = getMethod(joinPoint);
        log.info("[End] Method executed successfully: {}, return: {}", method.getName(), result);
    }

    @AfterThrowing(pointcut = "reservationPackage() && allRestControllers()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        Method method = getMethod(joinPoint);
        log.error("[Exception] method: {}, exception class: {}, exception message: {}", method.getName(), exception.getClass(), exception.getMessage());
        Object[] args = getParameters(joinPoint);
        for (Object arg : args) {
            log.error(">>[Exception] parameter type: {}, value: {}", arg.getClass().getSimpleName(), arg);
        }
        if (!(exception instanceof ReservationIllegalArgumentException) && !(exception instanceof NotFoundException)) {
            log.error(">>[Exception] stacktrace: ", exception);
        }
    }
}
