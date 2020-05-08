package com.ivyxjc.libra.core.flow

interface Workflow {

    fun getStatus(): WorkflowStatus

    companion object {
        fun create(): Workflow {
            return WorkflowDefaultImpl()
        }
    }
}

class WorkflowDefaultImpl : Workflow {
    private var status = WorkflowStatus.PREPARE

    override fun getStatus(): WorkflowStatus {
        return status
    }
}


enum class WorkflowStatus {
    PREPARE,
    START,
    DONE,
    TERMINATED
}