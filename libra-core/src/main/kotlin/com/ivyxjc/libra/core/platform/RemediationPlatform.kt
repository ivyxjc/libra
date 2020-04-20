package com.ivyxjc.libra.core.platform

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.config.UsecaseConfigService
import com.ivyxjc.libra.core.config.model.UsecaseConfig
import com.ivyxjc.libra.core.config.model.UsecaseType
import com.ivyxjc.libra.core.flow.Workflow
import com.ivyxjc.libra.core.flow.WorkflowSession
import com.ivyxjc.libra.core.flow.WorkflowStatus
import com.ivyxjc.libra.core.libraErrorMarker
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.processor.LibraProcessor
import com.ivyxjc.libra.core.retry.DelayRetryHandler
import com.ivyxjc.libra.core.retry.RetryType
import com.ivyxjc.libra.core.retry.exception.BlockedRetryException
import com.ivyxjc.libra.core.retry.exception.RetryException
import com.ivyxjc.libra.core.warnPlusMarker
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.LockSupport

/**
 * Should be Thread-safe
 */
class RemediationPlatform(val usecaseConfigService: UsecaseConfigService) : Dispatcher<UsecaseTxn> {
    private val pattern = LibraPattern.newPattern()

    companion object {
        private val log = loggerFor(RemediationPlatform::class.java)
    }

    @Suppress("DuplicatedCode")
    override fun dispatch(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") ucTxn: UsecaseTxn) {
        val sourceId = ucTxn.sourceId
        val usecaseConfig = usecaseConfigService.getProcess(ucTxn.usecaseName)
        val platformCallable = PlatformCallable(ucTxn, usecaseConfig, pattern)
        try {
            platformCallable.call()
        } catch (e: Exception) {
            val handler = DelayRetryHandler(log, ucTxn, platformCallable, RetryType.PLATFORM)
            do {
                var shouldStop = true
                // redundant check
                var compatibleRetryType = true
                try {
                    ucTxn.platformAttempt = ucTxn.platformAttempt + 1
                    shouldStop = handler.handleRetryException(e)
                } catch (e: Throwable) {
                    if (e is BlockedRetryException) {
                        compatibleRetryType = e.retryType == RetryType.PLATFORM
                        shouldStop = e.stopStrategy.shouldStop(ucTxn.platformAttempt)
                    } else if (e.cause is BlockedRetryException) {
                        val ec = e.cause as RetryException
                        compatibleRetryType = ec.retryType == RetryType.PLATFORM
                        shouldStop = ec.stopStrategy.shouldStop(ucTxn.platformAttempt)
                    }
                    if (e !is BlockedRetryException && e.cause !is BlockedRetryException) {
                        throw e
                    }
                    /**
                     * Redundant check, it should not meets incompatible retry type
                     * For Platform, just wrap it's message and throws it
                     */
                    if (!compatibleRetryType) {
                        log.error(libraErrorMarker, "Platform meets incompatible retry type", e)
                        throw RuntimeException(e.message)
                    }
                    if (shouldStop) {
                        log.error(
                                "[Usecase : {}] meets the stopStrategy in Remediation, platform attempt is {}",
                                usecaseConfig.name,
                                ucTxn.platformAttempt
                        )
                        throw RuntimeException(e.message)
                    }
                }
            } while (!shouldStop)
        } finally {
        }
    }

    private class PlatformCallable(
            val ucTxn: UsecaseTxn,
            val usecaseConfig: UsecaseConfig,
            val pattern: LibraPattern
    ) : Callable<Unit> {
        override fun call() {
            val flow = Workflow.create()
            val session = WorkflowSession.create()
            var index = 0
            var loopCount = 0
            loop@ while (true) {
                if (flow.getStatus() == WorkflowStatus.TERMINATED) {
                    break
                }
                var processor: LibraProcessor? = null
                when (usecaseConfig.type) {
                    UsecaseType.Simple -> {
                        if (index >= usecaseConfig.simpleProcessors.size) {
                            break@loop
                        }
                        processor = usecaseConfig.simpleProcessors[index]
                        index++
                    }
                    UsecaseType.Status -> {
                        processor = usecaseConfig.statusProcessorMap[ucTxn.usecaseStatus]
                    }
                }
                if (processor == null) {
                    log.error("failed to find the processor, the usecase txn is: {}", ucTxn)
                    TODO("save it into monitor")
                }
                pattern.process(ucTxn, processor, flow, session)
                if (loopCount > 50) {
                    log.warn(
                            warnPlusMarker,
                            "The RemediationPlatform loops for over 50 times, the loop count is {}, please look up into it.",
                            loopCount
                    )
                    loopCount++
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(3))
                }
            }
        }
    }
}

