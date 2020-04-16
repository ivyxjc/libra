package com.ivyxjc.libra.core.flow

class Workflow {
    var status = WorkflowStatus.PREPARE
}

enum class WorkflowStatus {
    PREPARE,
    START,
    DONE,
    TERMINATED
}