package com.ivyxjc.libra.core.models

import com.ivyxjc.libra.common.ErrorConstants
import com.ivyxjc.libra.core.exception.LibraMissingConfigException
import com.ivyxjc.libra.core.process.LibraProcessor
import org.apache.commons.lang3.StringUtils


//data class Workflow(val transformation: Transformation, val remediation: Remediation)


data class Transformation(val processors: List<LibraProcessor>) {
    companion object {
        @JvmStatic
        val EMPTY = Transformation(listOf())
    }
}

data class Remediation(val processors: List<LibraProcessor>)


class SourceConfig private constructor(
    val sourceId: Int,
    val transformationQueue: String,
    val transformation: Transformation,
    val usecases: Set<String>
) {
    class Builder {
        private var sourceId: Int = -1
        private var transformationQueue: String? = null
        private var transformation = Transformation.EMPTY
        private var usecases = mutableSetOf<String>()

        fun sourceId(sourceId: Int) = apply {
            this.sourceId = sourceId
        }

        fun transformationQueue(queue: String) = apply {
            this.transformationQueue = queue
        }

        fun transformation(transformation: Transformation) = apply {
            this.transformation = transformation
        }

        fun usecases(set: Set<String>) = apply {
            this.usecases.addAll(set)
        }


        fun build(): SourceConfig {
            if (StringUtils.isBlank(transformationQueue)) {
                throw LibraMissingConfigException(
                    "missing transformation",
                    ErrorConstants.SOURCE_CONFIG,
                    "transformationQueue"
                )
            }
            return SourceConfig(sourceId, transformationQueue!!, transformation, usecases)
        }
    }
}


