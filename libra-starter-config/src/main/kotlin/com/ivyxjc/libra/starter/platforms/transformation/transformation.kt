package com.ivyxjc.libra.starter.platforms.transformation

import com.ivyxjc.libra.core.config.SourceConfigService
import com.ivyxjc.libra.core.config.SourceConfigServiceMockImpl
import com.ivyxjc.libra.core.expose.BeansConstants
import com.ivyxjc.libra.core.platform.TransformationPlatform
import com.ivyxjc.libra.starter.common.model.LibraJmsListenerYaml
import com.ivyxjc.libra.starter.common.processors.AbstractLibraJmsAnnBeanPostProcessor
import com.ivyxjc.libra.starter.config.source.annotation.EnableLibraSourceConfig
import com.ivyxjc.libra.starter.config.utils.ConfigConstants
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Role
import org.springframework.jms.config.JmsListenerEndpointRegistry

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(LibraTransformationBootstrapConfiguration::class, LibraTransformationJmsListenersConfiguration::class)
@EnableLibraSourceConfig
annotation class EnableLibraTransformation


@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
open class LibraTransformationBootstrapConfiguration {

    @Bean
    open fun transformationPlatform(sourceConfigService: SourceConfigService): TransformationPlatform {
        return TransformationPlatform(sourceConfigService)
    }

    @Bean
    @ConditionalOnMissingBean(SourceConfigService::class)
    open fun sourceConfigService(): SourceConfigService {
        return SourceConfigServiceMockImpl()
    }
}

@Configuration
open class LibraTransformationJmsListenersConfiguration {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    open fun libraTransmissionJmsListenerAnnBeanPostProcessor(sourceConfigService: SourceConfigService)
            : LibraTransformationJmsListenerAnnBeanPostProcessor {
        return LibraTransformationJmsListenerAnnBeanPostProcessor(sourceConfigService)
    }

    @Bean
    open fun defaultJmsListenerEndpointRegistry(): JmsListenerEndpointRegistry? {
        return JmsListenerEndpointRegistry()
    }

}


class LibraTransformationJmsListenerAnnBeanPostProcessor(val sourceConfigService: SourceConfigService) :
        AbstractLibraJmsAnnBeanPostProcessor(ConfigConstants.TRANSFORMATION_JMS_NAME) {

    override fun processJmsListenerConfig(): List<LibraJmsListenerYaml> {
        val list = mutableListOf<LibraJmsListenerYaml>()
        val sourceConfigs = sourceConfigService.listAppConfigs()
        sourceConfigs.forEach {
            val tmpListenerYaml = LibraJmsListenerYaml()
            tmpListenerYaml.id = it.sourceId.toString()
            tmpListenerYaml.containerFactory = BeansConstants.INTERNAL_JMS_CONTAINER_FACTORY_NAME
            tmpListenerYaml.destination = it.transformationQueue
            tmpListenerYaml.messageListener = ConfigConstants.TRANSFORMATION_LISTENER
            tmpListenerYaml.dispatcher = ConfigConstants.TRANSFORMATION_PLATFORM
            tmpListenerYaml.concurrency = ConfigConstants.DEFAULT_CONCURRENCY
            list.add(tmpListenerYaml)
        }
        return list
    }

    override fun afterSingletonsInstantiated() {
    }
}
