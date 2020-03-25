package com.ivyxjc.libra.core.models

import com.ivyxjc.libra.core.process.LibraProcessor
import org.apache.commons.lang3.builder.ToStringBuilder


//data class Workflow(val transformation: Transformation, val remediation: Remediation)


data class Transformation(val processors: List<LibraProcessor>)

data class Remediation(val processors: List<LibraProcessor>)


class SourceConfig {
    var sourceId: Int = -1
    var transformationQueue: String? = null
    var transformation: Transformation = Transformation(listOf())

    // todo support multiple UsecaseConfig
    var usecases: List<UsecaseConfig> = mutableListOf()


    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}


