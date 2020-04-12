package com.ivyxjc.libra.core.platforms

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.models.UsecaseType
import com.ivyxjc.libra.core.process.LibraProcessor
import com.ivyxjc.libra.core.process.Workflow
import com.ivyxjc.libra.core.process.WorkflowSession
import com.ivyxjc.libra.core.process.WorkflowStatus
import com.ivyxjc.libra.core.service.UsecaseConfigService

/**
 * Should be Thread-safe
 */
class RemediationPlatform(val usecaseConfigService: UsecaseConfigService) : Dispatcher<UsecaseTxn> {
    private val pattern = LibraPattern.newPattern()

    companion object {
        private val log = loggerFor(RemediationPlatform::class.java)
    }

    override fun dispatch(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") ucTxn: UsecaseTxn) {
        val sourceId = ucTxn.sourceId
        val usecaseConfig = usecaseConfigService.getProcess(ucTxn.usecaseName)
        try {
            val flow = Workflow()
            val session = WorkflowSession()
            var index = 0
            var loopCount = 0
            loop@ while (true) {
                if (flow.status == WorkflowStatus.TERMINATED) {
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
                        "The RemediationPlatform loops for over 50 times, the loop count is {}, please look up into it.",
                        loopCount
                    )
                    loopCount++
                    Thread.sleep(500)
                }
            }

        } catch (e: Exception) {
            TODO("Libra Exception, Look up into the Libra Code")
        } finally {

        }
    }
}

