package xyz.ivyxjc.libra.core.mock.biz.processors

import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.core.models.UseCaseTxn
import xyz.ivyxjc.libra.core.process.LibraProcessor
import xyz.ivyxjc.libra.core.process.Workflow
import xyz.ivyxjc.libra.core.process.WorkflowSession

@Service("trans1Processor")
class Trans1Processor : LibraProcessor {
    override fun process(ucTxn: UseCaseTxn, flowStatus: Workflow, session: WorkflowSession) {
        TODO("not implemented")
    }
}

@Service("trans2Processor")
class Trans2Processor : LibraProcessor {
    override fun process(ucTxn: UseCaseTxn, flowStatus: Workflow, session: WorkflowSession) {
        TODO("not implemented")
    }
}

@Service("trans3Processor")
class Trans3Processor : LibraProcessor {
    override fun process(ucTxn: UseCaseTxn, flowStatus: Workflow, session: WorkflowSession) {
        TODO("not implemented")
    }
}
