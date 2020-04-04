package com.ivyxjc.libra.core.platforms

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.models.RawTransaction
import com.ivyxjc.libra.core.process.Workflow
import com.ivyxjc.libra.core.process.WorkflowSession
import com.ivyxjc.libra.core.process.WorkflowStatus
import com.ivyxjc.libra.core.rawTransToUcTxn
import com.ivyxjc.libra.core.service.SourceConfigService


/**
 * Should be Thread-safe
 */
class TransformationPlatform(val sourceConfigService: SourceConfigService) : Dispatcher<RawTransaction> {

    companion object {
        @JvmStatic
        private val log = loggerFor(TransformationPlatform::class.java)
    }

    override fun dispatch(rawTrans: RawTransaction) {
        val ucTxn = rawTransToUcTxn(rawTrans)
        val sourceId = ucTxn.sourceId

        val sourceConfig = sourceConfigService.getSourceConfig(sourceId)
        val transformation = sourceConfig!!.transformation
        var index = 0
        try {
            val flow = Workflow()
            val session = WorkflowSession()
            while (index < sourceConfig.transformation.size) {
                transformation[index].process(ucTxn, flow, session)
                index++
                if (flow.status != WorkflowStatus.TERMINATED) {
                    log.debug("status is {}, do the following processors.", flow.status)
                } else {
                    log.debug("status is terminated, skip the following processors")
                    break
                }
            }
        } catch (e: Exception) {
        } finally {
        }
    }
}

