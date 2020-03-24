package com.ivyxjc.libra.core.platforms

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.models.RawTransaction
import com.ivyxjc.libra.core.process.LibraProcessor
import com.ivyxjc.libra.core.process.Workflow
import com.ivyxjc.libra.core.process.WorkflowSession
import com.ivyxjc.libra.core.process.WorkflowStatus
import com.ivyxjc.libra.core.rawTransToUcTxn
import org.springframework.stereotype.Service


/**
 * Should be Thread-safe
 */
@Service("transformationPlatform")
class TransformationPlatform : Dispatcher<RawTransaction> {

    companion object {
        @JvmStatic
        private val log = loggerFor(TransformationPlatform::class.java)
    }

    override fun dispatch(rawTrans: RawTransaction) {
        val ucTxn = rawTransToUcTxn(rawTrans)
        val sourceId = ucTxn.sourceId

        val processors = mutableListOf<LibraProcessor>()
        var index = 0
        try {
            val flow = Workflow()
            val session = WorkflowSession()
            while (index < processors.size) {
                if (index < processors.size) {
                    processors[index].process(ucTxn, flow, session)
                }
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

