package xyz.ivyxjc.libra.core.platforms

import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.core.loggerFor
import xyz.ivyxjc.libra.core.models.RawTransaction
import xyz.ivyxjc.libra.core.process.LibraProcessor
import xyz.ivyxjc.libra.core.process.Workflow
import xyz.ivyxjc.libra.core.process.WorkflowSession
import xyz.ivyxjc.libra.core.process.WorkflowStatus
import xyz.ivyxjc.libra.core.rawTransToUcTxn


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
                    log.debug("status is ${flow.status}, do the following processors.")
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

