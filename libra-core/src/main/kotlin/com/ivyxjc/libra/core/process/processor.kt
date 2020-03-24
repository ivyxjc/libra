package com.ivyxjc.libra.core.process

import com.ivyxjc.libra.core.models.UseCaseTxn

interface LibraProcessor {

    fun process(ucTxn: UseCaseTxn, flowStatus: Workflow, session: WorkflowSession)
}

class BlankLibraProcessor : LibraProcessor {
    override fun process(ucTxn: UseCaseTxn, flowStatus: Workflow, session: WorkflowSession) {
        //do nothing
    }
}