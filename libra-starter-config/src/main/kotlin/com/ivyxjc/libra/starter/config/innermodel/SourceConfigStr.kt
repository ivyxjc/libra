package com.ivyxjc.libra.starter.config.source.model.inner

import org.apache.commons.lang3.builder.ToStringBuilder

internal class SourceConfigStr {
    var sourceId: Int = -1
    var transformationProcessor = mutableListOf<String>()
    var transformationQueue: String? = null

    // just support one Usecase now
    // todo support multiple UsecaseConfig
    var usecases = setOf<String>()

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}