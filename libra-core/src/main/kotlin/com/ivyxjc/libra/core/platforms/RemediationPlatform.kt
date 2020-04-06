package com.ivyxjc.libra.core.platforms

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.process.Workflow
import com.ivyxjc.libra.core.process.WorkflowSession
import com.ivyxjc.libra.core.service.UsecaseConfigService

/**
 * Should be Thread-safe
 */
class RemediationPlatform(val usecaseConfigService: UsecaseConfigService) : Dispatcher<UsecaseTxn> {

    companion object {
        private val log = loggerFor(RemediationPlatform::class.java)
    }

    override fun dispatch(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") ucTxn: UsecaseTxn) {
        val sourceId = ucTxn.sourceId
        val process = usecaseConfigService.getProcess(ucTxn.usecaseName)
        var index = 0
        try {
            val flow = Workflow()
            val session = WorkflowSession()
        } catch (e: Exception) {
        } finally {
        }
    }
}

