package com.ivyxjc.libra.starter.config.source.model.inner

import org.apache.commons.lang3.builder.ToStringBuilder

internal class UsecaseConfigStr {
    lateinit var name: String
    lateinit var type: String
    lateinit var queue: String

    private var simpleProcessors = mutableListOf<String>()
    private var statusProcessorMap = mutableMapOf<String, String>()


    fun addAllSimpleProcessors(list: List<String>) {
        simpleProcessors.addAll(list)
    }

    fun putAllStatusProcessorMap(map: Map<String, String>) {
        statusProcessorMap.putAll(map)
    }

    fun getSimpleProcessors(): List<String> {
        return simpleProcessors
    }

    fun getStatusProcessor(): Map<String, String> {
        return statusProcessorMap
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}