package com.ivyxjc.libra.core.platform

import com.google.common.util.concurrent.SimpleTimeLimiter
import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.flow.Workflow
import com.ivyxjc.libra.core.flow.WorkflowSession
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.processor.LibraProcessor
import com.ivyxjc.libra.core.retry.DelayRetryHandler
import com.ivyxjc.libra.core.retry.RetryType
import com.ivyxjc.libra.core.retry.exception.BlockedRetryException
import com.ivyxjc.libra.core.retry.exception.RetryException
import java.util.concurrent.Callable
import java.util.concurrent.Executors


interface LibraPattern {
    fun process(ucTxn: UsecaseTxn, processor: LibraProcessor, workflow: Workflow, session: WorkflowSession)

    companion object {
        @JvmStatic
        fun newPattern(): LibraPattern {
            return LibraPatternImpl()
        }

    }
}

class LibraPatternImpl : LibraPattern {

    private val timeLimiter = SimpleTimeLimiter.create(Executors.newCachedThreadPool())

    companion object {
        @JvmStatic
        private val log = loggerFor(LibraPattern::class.java)
    }

    @Suppress("DuplicatedCode")
    override fun process(
            ucTxn: UsecaseTxn,
            processor: LibraProcessor,
            workflow: Workflow,
            session: WorkflowSession
    ) {
        try {
            ucTxn.patternAttempt = 0
            processor.process(ucTxn, workflow, session)
        } catch (e: Throwable) {
            val processorCallable = ProcessorCallable(processor, ucTxn, workflow, session)
            val handler = DelayRetryHandler(log, ucTxn, processorCallable, RetryType.PROCESSOR)
            do {
                var shouldStop = true
                var compatibleRetryType = true
                try {
                    ucTxn.patternAttempt = ucTxn.patternAttempt + 1
                    shouldStop = handler.handleRetryException(e)
                } catch (e: Throwable) {
                    if (e is BlockedRetryException) {
                        compatibleRetryType = e.retryType == RetryType.PROCESSOR
                        if (compatibleRetryType) {
                            shouldStop = e.stopStrategy.shouldStop(ucTxn.patternAttempt)
                        }
                    } else if (e.cause is BlockedRetryException) {
                        val ec = e.cause as BlockedRetryException
                        compatibleRetryType = ec.retryType == RetryType.PROCESSOR
                        if (compatibleRetryType) {
                            shouldStop = ec.stopStrategy.shouldStop(ucTxn.patternAttempt)
                        }
                    }
                    if (e !is RetryException && e.cause !is RetryException) {
                        throw e
                    }
                    if (!compatibleRetryType) {
                        throw e
                    }
                    if (shouldStop) {
                        log.error(
                                "[{}] meets the stopStrategy, processor attempt is {}",
                                processor,
                                ucTxn.patternAttempt
                        )
                        throw RuntimeException(e.message)
                    }
                }
            } while (!shouldStop)
        } finally {

        }
    }

    private class ProcessorCallable(
            val processor: LibraProcessor,
            val ucTxn: UsecaseTxn,
            val workflow: Workflow,
            val session: WorkflowSession
    ) : Callable<Unit> {

        @Throws(Exception::class)
        override fun call() {
            processor.process(ucTxn, workflow, session)
        }
    }
}



