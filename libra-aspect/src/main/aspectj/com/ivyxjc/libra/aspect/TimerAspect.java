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

    //    @Pointcut("@annotation(com.ivyxjc.libra.aspect.LibraMetrics)")
    @Pointcut("execution( * com.ivyxjc.libra.core.platforms.TransmissionPlatform.dispatch(..))" +
            "|| execution(* com.ivyxjc.libra.core.endpoint.RawTransactionMessageListener.onMessage(..))" +
            "|| execution(* org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(..))" +
            "|| execution(* org.springframework.transaction.interceptor.TransactionAspectSupport.cleanupTransactionInfo(..))" +
            "|| execution(* org.springframework.transaction.interceptor.TransactionAspectSupport.commitTransactionAfterReturning(..))" +
            "|| execution(* org.springframework.transaction.interceptor.TransactionAspectSupport.TransactionInfo.bindToThread(..))" +
            "|| execution(* org.springframework.transaction.interceptor.TransactionAspectSupport.TransactionInfo.newTransactionStatus(..))" +
            "|| execution(* org.springframework.transaction.interceptor.TransactionAspectSupport.prepareTransactionInfo(..))" +
            "|| execution(* org.springframework.transaction.interceptor.TransactionAspectSupport.createTransactionIfNecessary(..))" +
            "|| execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.getTransaction(..))" +
            "|| execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.prepareSynchronization(..))" +
            "|| execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.handleExistingTransaction(..))" +
            "|| execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.suspend(..))" +
            "|| execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.newTransactionStatus(..))" +
            "|| execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.getTransactionSynchronization(..))" +

            "|| execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.processCommit(..))" +
            "|| execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.prepareForCommit(..))" +
            "|| execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.triggerBeforeCommit(..))" +
            "|| execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.triggerBeforeCompletion(..))" +
            "|| execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.doCommit(..))" +
            //active mq
            "|| execution(* org.apache.activemq.artemis.jms.client.ActiveMQSession.doCommit(..))" +
            "|| execution(* org.apache.activemq.artemis.core.client.impl.ClientSessionImpl.commit(..))" +

            "|| execution(* com.ivyxjc.libra.core.dao.RawTransMapper.insertRaw(..))" +

            "|| execution(* org.springframework.jms.connection.JmsTransactionManager.doBegin(..))" +
            "|| execution(* org.springframework.jms.connection.JmsTransactionManager.doGetTransaction(..))" +
            "|| execution(* org.springframework.jms.connection.JmsTransactionManager.isExistingTransaction(..))")
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
