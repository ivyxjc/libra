package com.ivyxjc.libra.core.platform

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.config.SourceConfigService
import com.ivyxjc.libra.core.config.model.SourceConfig
import com.ivyxjc.libra.core.flow.Workflow
import com.ivyxjc.libra.core.flow.WorkflowSession
import com.ivyxjc.libra.core.flow.WorkflowStatus
import com.ivyxjc.libra.core.libraErrorMarker
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.retry.DelayRetryHandler
import com.ivyxjc.libra.core.retry.RetryType
import com.ivyxjc.libra.core.retry.exception.BlockedRetryException
import com.ivyxjc.libra.core.retry.exception.RetryException
import java.util.concurrent.Callable


/**
 * Should be Thread-safe
 */
class TransformationPlatform(val sourceConfigService: SourceConfigService) : Dispatcher<UsecaseTxn> {

    private val pattern = LibraPattern.newPattern()

    companion object {
        @JvmStatic
        private val log = loggerFor(TransformationPlatform::class.java)
    }

    @Suppress("DuplicatedCode")
    override fun dispatch(ucTxn: UsecaseTxn) {
        val sourceId = ucTxn.sourceId

        val sourceConfig = sourceConfigService.getSourceConfig(sourceId)
        if (sourceConfig == null) {
            log.error("missing source config for source: {}", sourceId)
            /**
             * todo
             * Record it in monitor.
             * No need to throw exception because if there is no config about the source id,
             * to roll back it cannot make any difference. Just to consume it and record it
             * ,then notify Ops to replay the message.
             */
            return
        }
        val platformCallable = PlatformCallable(ucTxn, sourceConfig, pattern)
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
                    } else if (e.cause is RetryException) {
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
                                "[SourceId: {}] meets the stopStrategy in Transformation, platform attempt is {}",
                                sourceConfig.sourceId,
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
            val sourceConfig: SourceConfig,
            val pattern: LibraPattern
    ) : Callable<Unit> {
        override fun call() {
            val transformations = sourceConfig.transformation
            var index = 0
            val flow = Workflow.create()
            val session = WorkflowSession.create()
            while (index < transformations.size) {
                val processor = transformations[index]
                pattern.process(ucTxn, processor, flow, session)
                index++
                if (flow.getStatus() != WorkflowStatus.TERMINATED) {
                    log.debug("status is {}, do the following processors.", flow.getStatus())
                } else {
                    log.debug("status is terminated, skip the following processors")
                    break
                }
            }
        }
    }

}

