package com.ivyxjc.libra.core.processor

import com.ivyxjc.libra.core.flow.Workflow
import com.ivyxjc.libra.core.flow.WorkflowSession
import com.ivyxjc.libra.core.models.UsecaseTxn

interface LibraProcessor {

    fun process(ucTxn: UsecaseTxn, flowStatus: Workflow, session: WorkflowSession)
}

class BlankLibraProcessor : LibraProcessor {
    override fun process(ucTxn: UsecaseTxn, flowStatus: Workflow, session: WorkflowSession) {
        //do nothing
    }
}