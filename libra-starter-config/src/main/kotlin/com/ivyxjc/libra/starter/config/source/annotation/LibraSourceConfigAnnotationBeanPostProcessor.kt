package com.ivyxjc.libra.starter.config.source.annotation

import com.ivyxjc.libra.common.ErrorConstants
import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.exception.LibraConfigConflictException
import com.ivyxjc.libra.core.models.SourceConfig
import com.ivyxjc.libra.core.models.Transformation
import com.ivyxjc.libra.core.models.UsecaseConfig
import com.ivyxjc.libra.core.process.LibraProcessor
import com.ivyxjc.libra.core.service.SourceConfigService
import com.ivyxjc.libra.starter.config.source.model.inner.SourceConfigStr
import com.ivyxjc.libra.starter.config.source.model.inner.xsds.XsdUtils
import com.ivyxjc.libra.starter.config.source.utils.ConfigConstants
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.SmartInitializingSingleton

class LibraSourceConfigAnnotationBeanPostProcessor : BeanFactoryAware, SmartInitializingSingleton {
    companion object {
        private val log =
            loggerFor(LibraSourceConfigAnnotationBeanPostProcessor::class.java)
    }

    private var beanFactory: BeanFactory? = null

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    override fun afterSingletonsInstantiated() {
        log.info("start loading source config")
        loadConfig()
        log.info("loading source config ends successfully")
    }


    private fun loadConfig() {
        // todo just use a static method for temp usage
        val libraFlowConfig = XsdUtils.parseXml("source-config.xml")
        val pair = XsdUtils.parse(libraFlowConfig)
        val sourceConfigService = this.beanFactory!!.getBean(SourceConfigService::class.java)

        val sourceConfigStrMap = mutableMapOf<Int, SourceConfigStr>()

        val usecaseMap = mutableMapOf<String, UsecaseConfig>()

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
            usecaseMap[it.name] = UsecaseConfig(it.name, it.type, simpleProcessors, statusProcessorMap)
        }

        pair.first.forEach {
            val sourceConfig = SourceConfig()
            sourceConfig.sourceId = it.sourceId
            sourceConfig.transformation = getTransformations(it.transformationProcessor)
            sourceConfig.usecases = getUsecases(it.usecases, usecaseMap);
            sourceConfigService.registerConfig(it.sourceId, sourceConfig)
        }
    }

    private fun getUsecases(list: List<String>, usecaseMap: Map<String, UsecaseConfig>): List<UsecaseConfig> {
        val res = mutableListOf<UsecaseConfig>()
        list.forEach {
            res.add(usecaseMap.getValue(it))
        }
        return res
    }

    private fun getTransformations(list: List<String>): Transformation {
        val processors = mutableListOf<LibraProcessor>()
        list.forEach {
            processors.add(getBean(it))
        }
        return Transformation(processors)
    }

    private fun getBean(beanName: String): LibraProcessor {
        val obj = this.beanFactory!!.getBean(beanName)
        if (obj !is LibraProcessor) {
            throw LibraConfigConflictException(
                "SourceConfig transformation processors must be type LibraProcessor",
                ErrorConstants.SOURCE_CONFIG,
                "Transformation"
            )
        }
        return obj
    }
}
