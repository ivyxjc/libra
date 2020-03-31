package com.ivyxjc.libra.aspect;

import com.ivyxjc.libra.common.proxy.LoggerProxy;
import com.ivyxjc.libra.common.utils.CoreCommons;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.concurrent.TimeUnit;

@Aspect
public class TimerAspect {

    private static final LoggerProxy log = CoreCommons.loggerFor(TimerAspect.class);

    @Pointcut("@annotation(com.ivyxjc.libra.aspect.LibraMetrics)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = StopWatch.createStarted();
        Object res = joinPoint.proceed();
        stopWatch.stop();
        log.debug("TimerAspect: method is [{}], costs {}", joinPoint, stopWatch.getTime(TimeUnit.MICROSECONDS));
        return res;
    }
}
