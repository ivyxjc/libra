package com.ivyxjc.libra.starter.platforms.transmission

import com.ivyxjc.libra.core.CoreCommons
import com.ivyxjc.libra.core.CorePosition
import com.ivyxjc.libra.core.dao.RawTransMapper
import com.ivyxjc.libra.core.platforms.TransmissionPlatform
import com.ivyxjc.libra.core.service.SourceConfigService
import com.ivyxjc.libra.core.service.SourceConfigServiceMockImpl
import com.ivyxjc.libra.starter.common.model.LibraJmsListenerYaml
import com.ivyxjc.libra.starter.common.processors.AbstractLibraJmsAnnBeanPostProcessor
import com.ivyxjc.libra.starter.config.source.annotation.EnableLibraSourceConfig
import com.ivyxjc.libra.starter.config.utils.ConfigConstants
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Role
import org.springframework.jms.config.JmsListenerEndpointRegistry
import org.springframework.jms.core.JmsTemplate
import org.yaml.snakeyaml.Yaml
import javax.jms.ConnectionFactory

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(LibraTransmissionBootstrapConfiguration::class, LibraTransmissionJmsListenersConfiguration::class)
@EnableLibraSourceConfig
annotation class EnableLibraTransmission


@Configuration
@AutoConfigureOrder(0)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
open class LibraTransmissionBootstrapConfiguration {

    @Bean
    open fun transmissionPlatform(
        sourceConfigService: SourceConfigService,
        @Qualifier(CoreCommons.BeansConstants.INTERNAL_JMS_CONNECTION_FACTORY_NAME) connectionFactory: ConnectionFactory,
        rawTransMapper: RawTransMapper,
        jmsTemplate: JmsTemplate
    ): TransmissionPlatform {
        return TransmissionPlatform(sourceConfigService, connectionFactory, rawTransMapper, jmsTemplate)
    }

    @Bean
    @ConditionalOnMissingBean(SourceConfigService::class)
    open fun sourceConfigService(): SourceConfigService {
        return SourceConfigServiceMockImpl()
    }


}

@Configuration
@AutoConfigureOrder(1)
open class LibraTransmissionJmsListenersConfiguration {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    open fun libraTransmissionJmsListenerAnnBeanPostProcessor(sourceConfigService: SourceConfigService)
            : LibraTransmissionJmsListenerAnnBeanPostProcessor {
        return LibraTransmissionJmsListenerAnnBeanPostProcessor(sourceConfigService)
    }


    @Bean
    open fun defaultJmsListenerEndpointRegistry(): JmsListenerEndpointRegistry? {
        return JmsListenerEndpointRegistry()
    }

}


class LibraTransmissionJmsListenerAnnBeanPostProcessor(val sourceConfigService: SourceConfigService) :
    AbstractLibraJmsAnnBeanPostProcessor(ConfigConstants.TRANSMISSION_JMS_NAME) {

    override fun processJmsListenerConfig(): List<LibraJmsListenerYaml> {
        val yaml = Yaml()
        val input = CorePosition::class.java.classLoader.getResourceAsStream("endpoint.yaml")
        val res = mutableListOf<LibraJmsListenerYaml>()
        input.use {
            val endpointMap: Map<String, *> = yaml.load(input)

            @Suppress("UNCHECKED_CAST")
            val endpointListenersMap = endpointMap["endpointListeners"] as? Map<String, Map<String, String>>
            endpointListenersMap?.forEach {
                val tmpListenerYaml = LibraJmsListenerYaml()
                val listenerMap = it.value
                tmpListenerYaml.id = it.key
                tmpListenerYaml.subscription = listenerMap["subscription"] ?: ""
                tmpListenerYaml.selector = listenerMap["selector"] ?: ""
                tmpListenerYaml.concurrency = listenerMap["concurrency"] ?: "4-4"
                tmpListenerYaml.sourceIds = listenerMap["sourceIds"] ?: "ALL"
                tmpListenerYaml.containerFactory = listenerMap.getValue("containerFactory")
                tmpListenerYaml.destination = listenerMap.getValue("destination")
                tmpListenerYaml.messageListener = listenerMap.getValue("messageListener")
                tmpListenerYaml.dispatcher = listenerMap.getValue("dispatcher")
                res.add(tmpListenerYaml)
            }
        }
        return res
    }

    override fun afterSingletonsInstantiated() {
    }
}