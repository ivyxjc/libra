package com.ivyxjc.libra.core.config.model

import com.ivyxjc.libra.core.config.exception.LibraMissingConfigException
import com.ivyxjc.libra.core.expose.ErrorConstants
import com.ivyxjc.libra.core.processor.LibraProcessor
import org.apache.commons.lang3.StringUtils


//data class Workflow(val transformation: Transformation, val remediation: Remediation)


data class Transformation(val processors: List<LibraProcessor>) : Iterable<LibraProcessor>, List<LibraProcessor> {
    companion object {
        @JvmStatic
        val EMPTY = Transformation(listOf())
    }

    override fun iterator(): Iterator<LibraProcessor> = processors.iterator()

    override val size: Int
        get() = processors.size

    override fun contains(element: LibraProcessor): Boolean = processors.contains(element)

    override fun containsAll(elements: Collection<LibraProcessor>): Boolean = processors.containsAll(elements)

    override fun get(index: Int): LibraProcessor = processors[index]

    override fun indexOf(element: LibraProcessor): Int = processors.indexOf(element)

    override fun isEmpty(): Boolean = processors.isEmpty()

    override fun lastIndexOf(element: LibraProcessor): Int = processors.lastIndexOf(element)

    override fun listIterator(): ListIterator<LibraProcessor> = processors.listIterator()

    override fun listIterator(index: Int): ListIterator<LibraProcessor> = processors.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<LibraProcessor> = processors.subList(fromIndex, toIndex)
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


