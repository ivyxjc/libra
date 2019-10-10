package xyz.ivyxjc.libra.core.models


//data class Workflow(val transformation: Transformation, val remediation: Remediation)

data class Processor(val name: String)

data class Transformation(val processors: List<Processor>)

data class Remediation(val processors: List<RemediationProcessor>)

data class RemediationProcessor(val status: String, val processor: String)

class SourceConfig {
    var sourceId: Long = -1
    var transformationQueue: String? = null
    var transformation: Transformation? = null
    var remediation: Remediation? = null
}

