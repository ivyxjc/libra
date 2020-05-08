package com.ivyxjc.libra.core.retry

import com.google.common.util.concurrent.SimpleTimeLimiter
import com.ivyxjc.libra.common.log.LoggerProxy
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.retry.exception.AsyncInMemoryRetryException
import com.ivyxjc.libra.core.retry.exception.AsyncRetryException
import com.ivyxjc.libra.core.retry.exception.BlockedRetryException
import com.ivyxjc.libra.core.retry.exception.RetryException
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DelayRetryHandler(
        private val log: LoggerProxy,
        private val ucTxn: UsecaseTxn,
        private val callable: Callable<Unit>,
        private val supportRetryType: RetryType
) {
    private val timeLimiter = SimpleTimeLimiter.create(Executors.newCachedThreadPool())

    @Throws(Throwable::class)
    fun handleRetryException(e: Throwable): Boolean {
        var shouldStop = false
        when (e) {
            is BlockedRetryException -> {
                if (supportRetryType == e.retryType) {
                    log.warn("ucTxn {} throws BlockedRetryException, try to retry", ucTxn)
                    callable.call()
                    shouldStop = true
                } else {
                    throw e
                }
            }
            is AsyncRetryException -> {
                TODO("not implemented")
            }
            is AsyncInMemoryRetryException -> {
                if (supportRetryType == e.retryType) {
                    timeLimiter.callWithTimeout(callable, 10, TimeUnit.SECONDS)
                } else {
                    throw e
                }
            }
            else -> {
                throw e
            }
        }
        if (shouldStop) {
            return shouldStop
        }
        return if (e is RetryException) {
            when {
                RetryType.PLATFORM == e.retryType -> {
                    e.stopStrategy.shouldStop(ucTxn.platformAttempt)
                }
                RetryType.PROCESSOR == e.retryType -> {
                    e.stopStrategy.shouldStop(ucTxn.patternAttempt)
                }
                else -> {
                    log.error("Retry type is not in [PLATFORM, PROCESSOR], actual retry type is: {}", e.retryType)
                    throw e
                }
            }
        } else {
            throw e
        }
    }
}