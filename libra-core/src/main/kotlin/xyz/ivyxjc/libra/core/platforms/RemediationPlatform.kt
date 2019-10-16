package xyz.ivyxjc.libra.core.platforms

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.common.utils.loggerFor
import xyz.ivyxjc.libra.core.models.UseCaseTxn
import xyz.ivyxjc.libra.core.process.Workflow
import xyz.ivyxjc.libra.core.process.WorkflowSession
import xyz.ivyxjc.libra.core.service.UseCaseService

/**
 * Should be Thread-safe
 */
@Service("remediationPlatform")
class RemediationPlatform : Dispatcher<UseCaseTxn> {

    @Autowired
    private lateinit var mUseCaseService: UseCaseService

    companion object {
        private val log = loggerFor(RemediationPlatform::class.java)
    }

    override fun dispatch(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") ucTxn: UseCaseTxn) {
        val sourceId = ucTxn.sourceId
        val process = mUseCaseService.getProcess(ucTxn)
        var index = 0
        try {
            val flow = Workflow()
            val session = WorkflowSession()
        } catch (e: Exception) {
        } finally {
        }
    }
}

