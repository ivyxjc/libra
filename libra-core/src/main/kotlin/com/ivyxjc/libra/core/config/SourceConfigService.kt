package com.ivyxjc.libra.core.config

import com.ivyxjc.libra.core.config.model.SourceConfig


interface SourceConfigService {
    fun getSourceConfig(sourceId: Int): SourceConfig?
    fun registerConfig(sourceId: Int, config: SourceConfig)
    fun listAppSources(): Set<Int>
    fun listAppConfigs(): List<SourceConfig>
}

class SourceConfigServiceMockImpl : SourceConfigService {

    private var sourceMap = mutableMapOf<Int, SourceConfig>()

    override fun registerConfig(sourceId: Int, config: SourceConfig) {
        sourceMap[sourceId] = config
    }

    override fun getSourceConfig(sourceId: Int): SourceConfig? {
        return sourceMap[sourceId]
    }

    override fun listAppSources(): Set<Int> {
        return sourceMap.keys
    }

    override fun listAppConfigs(): List<SourceConfig> {
        return sourceMap.values.toList()
    }
}

