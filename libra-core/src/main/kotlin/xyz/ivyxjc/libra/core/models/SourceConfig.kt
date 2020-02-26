package xyz.ivyxjc.libra.core.models

import org.apache.commons.lang3.builder.ToStringBuilder
import xyz.ivyxjc.libra.core.process.LibraProcessor


//data class Workflow(val transformation: Transformation, val remediation: Remediation)


data class Transformation(val processors: List<LibraProcessor>)

data class Remediation(val processors: List<LibraProcessor>)


class SourceConfig {
    var sourceId: Long = -1
    var transformationQueue: String? = null
    var transformation: Transformation = Transformation(listOf())
    var remediation: Remediation? = Remediation(listOf())
    var usecases: List<String>? = null
    var status: List<String>? = null


    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}

