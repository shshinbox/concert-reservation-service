package me.songha.concert.shared.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public abstract class AbstractCommonAspect {
    @Pointcut("execution(* me.songha.concert.reservation..*.*(..))")
    protected void reservationPackage() {
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    protected void allRestControllers() {
    }

    protected Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    protected Object[] getParameter(JoinPoint joinPoint) {
        return joinPoint.getArgs();
    }
}
