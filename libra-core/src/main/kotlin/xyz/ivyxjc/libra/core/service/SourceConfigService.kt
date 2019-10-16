package xyz.ivyxjc.libra.core.service

import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.core.models.SourceConfig
import javax.annotation.PostConstruct


interface SourceConfigService {
    fun getSourceConfig(sourceId: Long): SourceConfig
}

@Service
class SourceConfigServiceMockImpl : SourceConfigService {

    @PostConstruct
    fun postConstruct() {
    }


    override fun getSourceConfig(sourceId: Long): SourceConfig {
        val sc = SourceConfig()
        sc.sourceId = sourceId
        sc.transformationQueue = "IVY.TRANSFORMATION1"
        return sc;
    }
}