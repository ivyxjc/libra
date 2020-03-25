package com.ivyxjc.libra.core.mock.biz.processors

import com.ivyxjc.libra.core.models.UseCaseTxn
import com.ivyxjc.libra.core.process.LibraProcessor
import com.ivyxjc.libra.core.process.Workflow
import com.ivyxjc.libra.core.process.WorkflowSession
import org.springframework.stereotype.Service


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
