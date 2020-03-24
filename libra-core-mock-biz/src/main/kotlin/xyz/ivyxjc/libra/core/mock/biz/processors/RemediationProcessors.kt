package xyz.ivyxjc.libra.core.mock.biz.processors

import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.core.models.UseCaseTxn
import xyz.ivyxjc.libra.core.process.LibraProcessor
import xyz.ivyxjc.libra.core.process.Workflow
import xyz.ivyxjc.libra.core.process.WorkflowSession


@Service
class Uc1Processor : LibraProcessor {
    override fun process(ucTxn: UseCaseTxn, flowStatus: Workflow, session: WorkflowSession) {
        TODO("not implemented")
    }
}

@Service
class Uc2Processor : LibraProcessor {
    override fun process(ucTxn: UseCaseTxn, flowStatus: Workflow, session: WorkflowSession) {
        TODO("not implemented")
    }
}

@Service("uc3Processor")
class Reme3Processor : LibraProcessor {
    override fun process(ucTxn: UseCaseTxn, flowStatus: Workflow, session: WorkflowSession) {
        TODO("not implemented")
    }
}
