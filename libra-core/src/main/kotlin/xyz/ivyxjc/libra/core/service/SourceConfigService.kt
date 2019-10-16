package xyz.ivyxjc.libra.core.service

import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.core.models.SourceConfig


interface SourceConfigService {
    fun getSourceConfig(sourceId: Long): SourceConfig
}

@Service
class SourceConfigServiceMockImpl : SourceConfigService {


    override fun getSourceConfig(sourceId: Long): SourceConfig {
        val sc = SourceConfig()
        sc.sourceId = sourceId
        sc.transformationQueue = "IVY.TRANS10"
        return sc
    }
}