package com.ivyxjc.libra.core.service

import com.ivyxjc.libra.core.models.UsecaseConfig
import org.springframework.stereotype.Service

interface UseCaseConfigService {
    fun registerConfig(name: String, config: UsecaseConfig)
    fun getProcess(name: String): UsecaseConfig
}

@Service
class UsecaseConfigServiceMockImpl : UseCaseConfigService {
    private var usecaseMap = mutableMapOf<String, UsecaseConfig>()

    override fun registerConfig(name: String, config: UsecaseConfig) {
        usecaseMap[name] = config
    }

    override fun getProcess(name: String): UsecaseConfig {
        return usecaseMap[name]!!
    }
}


class UseCaseConfigServiceImpl : UseCaseConfigService {

    override fun registerConfig(name: String, config: UsecaseConfig) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProcess(name: String): UsecaseConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
