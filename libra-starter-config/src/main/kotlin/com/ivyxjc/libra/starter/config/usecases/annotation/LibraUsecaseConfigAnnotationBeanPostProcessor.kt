package com.ivyxjc.libra.starter.config.usecases.annotation

import com.ivyxjc.libra.common.ErrorConstants
import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.exception.LibraConfigConflictException
import com.ivyxjc.libra.core.models.UsecaseConfig
import com.ivyxjc.libra.core.process.LibraProcessor
import com.ivyxjc.libra.core.service.UseCaseConfigService
import com.ivyxjc.libra.starter.config.utils.ConfigConstants
import com.ivyxjc.libra.starter.config.utils.XsdUtils
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.SmartInitializingSingleton

class LibraUsecaseConfigAnnotationBeanPostProcessor : BeanFactoryAware, SmartInitializingSingleton {

    companion object {
        private val log = loggerFor(LibraUsecaseConfigAnnotationBeanPostProcessor::class.java)
    }

    private var beanFactory: BeanFactory? = null


    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    override fun afterSingletonsInstantiated() {
        log.info("start loading usecase config")
        loadConfig()
        log.info("loading usecase config ends successfully")

    }

    private fun loadConfig() {
        // todo just use a static method for temp usage
        val libraFlowConfig = XsdUtils.parseXml("source-config.xml")
        val pair = XsdUtils.parse(libraFlowConfig)
        val usecaseConfigService = this.beanFactory!!.getBean(UseCaseConfigService::class.java)
        val usecases = mutableListOf<UsecaseConfig>()

        pair.second.forEach {
            val simpleProcessors = mutableListOf<LibraProcessor>()
            val statusProcessorMap = mutableMapOf<String, LibraProcessor>()
            when (it.type) {
                ConfigConstants.USECASE_TYPE_SIMPLE -> it.getSimpleProcessors().forEach { p ->
                    simpleProcessors.add(getBean(p))
                }
                ConfigConstants.USECASE_TYPE_STATUS -> it.getStatusProcessor().forEach { p ->
                    statusProcessorMap[p.key] = getBean(p.value)
                }
                else -> throw LibraConfigConflictException(
                    "Usecase Type must be one of [simple, status]",
                    ErrorConstants.SOURCE_CONFIG,
                    "Usecase"
                )
            }
            usecases.add(
                UsecaseConfig.Builder()
                    .name(it.name)
                    .type(it.type)
                    .queue(it.queue)
                    .simpleProcessors(simpleProcessors)
                    .statusProcessorMap(statusProcessorMap)
                    .build()
            )
        }
        usecases.forEach {
            usecaseConfigService.registerConfig(it.name, it)
        }
    }

    private fun getBean(beanName: String): LibraProcessor {
        return beanFactory!!.getBean(beanName) as? LibraProcessor ?: throw LibraConfigConflictException(
            "UsecaseConfig processors must be type LibraProcessor",
            ErrorConstants.USECASE_CONFIG,
            beanName
        )
    }
}