package com.ivyxjc.libra.core.platforms

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.models.UseCaseTxn
import com.ivyxjc.libra.core.process.Workflow
import com.ivyxjc.libra.core.process.WorkflowSession
import com.ivyxjc.libra.core.service.UseCaseConfigService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Should be Thread-safe
 */
@Service("remediationPlatform")
class RemediationPlatform : Dispatcher<UseCaseTxn> {

    @Autowired
    private lateinit var mUseCaseConfigService: UseCaseConfigService

    companion object {
        private val log = loggerFor(RemediationPlatform::class.java)
    }

    override fun dispatch(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") ucTxn: UseCaseTxn) {
        val sourceId = ucTxn.sourceId
        val process = mUseCaseConfigService.getProcess(ucTxn.usecaseName)
        var index = 0
        try {
            val flow = Workflow()
            val session = WorkflowSession()
        } catch (e: Exception) {
        } finally {
        }
    }
}

