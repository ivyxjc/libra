package xyz.ivyxjc.libra.core.process

import xyz.ivyxjc.libra.core.models.UsecaseTxn

interface LibraProcessor {

    fun process(ucTxn: UsecaseTxn, flowStatus: Workflow, session: WorkflowSession)
}

class BlankLibraProcessor : LibraProcessor {
    override fun process(ucTxn: UsecaseTxn, flowStatus: Workflow, session: WorkflowSession) {
        //do nothing
    }
}