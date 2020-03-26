package com.ivyxjc.libra.starter.config.source.annotation

import com.ivyxjc.libra.common.ErrorConstants
import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.exception.LibraConfigConflictException
import com.ivyxjc.libra.core.models.SourceConfig
import com.ivyxjc.libra.core.models.Transformation
import com.ivyxjc.libra.core.process.LibraProcessor
import com.ivyxjc.libra.core.service.SourceConfigService
import com.ivyxjc.libra.starter.config.utils.XsdUtils
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
        val sourceConfigs = mutableListOf<SourceConfig>()

        pair.first.forEach {
            val sourceConfig = SourceConfig.Builder()
                .sourceId(it.sourceId)
                .transformation(buildTransformations(it.transformationProcessor))
                .transformationQueue(it.transformationQueue!!)
                .usecases(it.usecases)
                .build()
            sourceConfigs.add(sourceConfig)
        }
        sourceConfigs.forEach {
            sourceConfigService.registerConfig(it.sourceId, it)
        }
    }

    private fun buildTransformations(list: List<String>): Transformation {
        val processors = mutableListOf<LibraProcessor>()
        list.forEach {
            processors.add(getBean(it))
        }
        return Transformation(processors)
    }

    private fun getBean(beanName: String): LibraProcessor {
        return beanFactory!!.getBean(beanName) as? LibraProcessor ?: throw LibraConfigConflictException(
            "SourceConfig transformation processors must be type LibraProcessor",
            ErrorConstants.SOURCE_CONFIG,
            "Transformation"
        )
    }
}
