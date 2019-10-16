package xyz.ivyxjc.libra.core.platforms

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.common.utils.loggerFor
import xyz.ivyxjc.libra.core.models.UsecaseTxn
import xyz.ivyxjc.libra.core.process.Workflow
import xyz.ivyxjc.libra.core.process.WorkflowSession
import xyz.ivyxjc.libra.core.service.UsecaseService

/**
 * Should be Thread-safe
 */
@Service("remediationPlatform")
class RemediationPlatform : Dispatcher<UsecaseTxn> {

    @Autowired
    private lateinit var usecaseService: UsecaseService

    companion object {
        private val log = loggerFor(RemediationPlatform::class.java)
    }

    override fun dispatch(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") ucTxn: UsecaseTxn) {
        val sourceId = ucTxn.sourceId
        val process = usecaseService.getProcess(ucTxn)
        var index = 0
        try {
            val flow = Workflow()
            val session = WorkflowSession()
        } catch (e: Exception) {
        } finally {
        }
    }
}

