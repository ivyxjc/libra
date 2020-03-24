package com.ivyxjc.libra.core.mock.biz.processors

import com.ivyxjc.libra.core.models.UseCaseTxn
import com.ivyxjc.libra.core.process.LibraProcessor
import com.ivyxjc.libra.core.process.Workflow
import com.ivyxjc.libra.core.process.WorkflowSession
import org.springframework.stereotype.Service

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
