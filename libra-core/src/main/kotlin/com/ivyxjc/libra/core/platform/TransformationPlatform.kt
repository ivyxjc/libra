package com.ivyxjc.libra.core.platform

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.config.SourceConfigService
import com.ivyxjc.libra.core.expose.UcTxnAttributeConstants
import com.ivyxjc.libra.core.flow.Workflow
import com.ivyxjc.libra.core.flow.WorkflowSession
import com.ivyxjc.libra.core.flow.WorkflowStatus
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.retry.exception.DelayRetryInMemoryException
import com.ivyxjc.libra.core.retry.exception.InstantRetryException


/**
 * Should be Thread-safe
 */
class TransformationPlatform(val sourceConfigService: SourceConfigService) : Dispatcher<UsecaseTxn> {

    private val libraPattern = LibraPattern.newPattern()

    companion object {
        @JvmStatic
        private val log = loggerFor(TransformationPlatform::class.java)
    }

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
        val transformations = sourceConfig.transformation
        var index = 0
        try {
            val flow = Workflow()
            val session = WorkflowSession()
            while (index < transformations.size) {
                val processor = transformations[index]
                libraPattern.process(ucTxn, processor, flow, session)
                index++
                if (flow.status != WorkflowStatus.TERMINATED) {
                    log.debug("status is {}, do the following processors.", flow.status)
                } else {
                    log.debug("status is terminated, skip the following processors")
                    break
                }
            }
        } catch (e: Exception) {
            when (e) {
                is InstantRetryException -> {
                    ucTxn.attributes[UcTxnAttributeConstants.RETRY_ATTEMPT] = e.count.toString()
                    dispatch(ucTxn)
                }
                is DelayRetryInMemoryException -> {
                    val attempt = ucTxn.attributes[UcTxnAttributeConstants.RETRY_ATTEMPT]!!.toInt()
                    ucTxn.attributes[UcTxnAttributeConstants.RETRY_ATTEMPT] = (attempt + 1).toString()
                    val shouldStop = e.stopStrategy.shouldStop(attempt)
                    val computeSleepTime = e.waitStrategy.computeSleepTime(attempt)
                    TODO("not implemented")
                }
                else -> {
                    // todo exception handler
                    log.error("LibraPattern throws Not-Retryable exception", e)
                }
            }
        } finally {
        }
    }
}

