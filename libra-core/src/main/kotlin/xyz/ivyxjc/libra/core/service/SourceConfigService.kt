package xyz.ivyxjc.libra.core.service

import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.core.models.SourceConfig


interface SourceConfigService {
    fun getSourceConfig(sourceId: Int): SourceConfig?
    fun registerConfig(sourceId: Int, config: SourceConfig)
}

@Service
class SourceConfigServiceMockImpl : SourceConfigService {

    private var sourceMap = mutableMapOf<Int, SourceConfig>()

    override fun registerConfig(sourceId: Int, config: SourceConfig) {
        sourceMap[sourceId] = config
    }

    override fun getSourceConfig(sourceId: Int): SourceConfig? {
        return sourceMap[sourceId]
    }
}