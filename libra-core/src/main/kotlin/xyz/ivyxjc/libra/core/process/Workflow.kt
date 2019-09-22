package xyz.ivyxjc.libra.core.process

class Workflow {
    var status = WorkflowStatus.PREPARE
}

enum class WorkflowStatus {
    PREPARE,
    START,
    DONE,
    TERMINATED
}