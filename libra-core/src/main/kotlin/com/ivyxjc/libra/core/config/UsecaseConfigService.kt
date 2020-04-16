package com.ivyxjc.libra.core.config

import com.ivyxjc.libra.core.config.model.UsecaseConfig

interface UsecaseConfigService {
    fun registerConfig(name: String, config: UsecaseConfig)
    fun getProcess(name: String): UsecaseConfig
    fun listAppUsecases(): Set<UsecaseConfig>
}

class UsecaseConfigServiceMockImpl : UsecaseConfigService {
    private var usecaseMap = mutableMapOf<String, UsecaseConfig>()

    override fun registerConfig(name: String, config: UsecaseConfig) {
        usecaseMap[name] = config
    }

    override fun getProcess(name: String): UsecaseConfig {
        return usecaseMap[name]!!
    }

    override fun listAppUsecases(): Set<UsecaseConfig> {
        return usecaseMap.values.toSet()
    }
}


class UsecaseConfigServiceImpl : UsecaseConfigService {

    override fun registerConfig(name: String, config: UsecaseConfig) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProcess(name: String): UsecaseConfig {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listAppUsecases(): Set<UsecaseConfig> {
        TODO("Not yet implemented")
    }
}
