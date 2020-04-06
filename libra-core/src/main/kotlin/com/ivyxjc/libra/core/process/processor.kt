package com.ivyxjc.libra.core.process

import com.ivyxjc.libra.core.models.UsecaseTxn

interface LibraProcessor {

    fun process(ucTxn: UsecaseTxn, flowStatus: Workflow, session: WorkflowSession)
}

class BlankLibraProcessor : LibraProcessor {
    override fun process(ucTxn: UsecaseTxn, flowStatus: Workflow, session: WorkflowSession) {
        //do nothing
    }
}