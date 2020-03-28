package com.ivyxjc.libra.starter.platforms.transformation

import com.ivyxjc.libra.core.CoreCommons
import com.ivyxjc.libra.core.platforms.TransformationPlatform
import com.ivyxjc.libra.core.service.SourceConfigService
import com.ivyxjc.libra.core.service.SourceConfigServiceMockImpl
import com.ivyxjc.libra.starter.common.model.LibraJmsListenerYaml
import com.ivyxjc.libra.starter.common.processors.AbstractLibraJmsAnnBeanPostProcessor
import com.ivyxjc.libra.starter.config.utils.ConfigConstants
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Role
import org.springframework.jms.annotation.EnableJms

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(LibraTransformationBootstrapConfiguration::class)
@EnableJms
annotation class EnableLibraTransformation


@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
open class LibraTransformationBootstrapConfiguration {

    @Bean
    open fun transformationPlatform(sourceConfigService: SourceConfigService): TransformationPlatform {
        return TransformationPlatform(sourceConfigService)
    }

    @Bean
    open fun sourceConfigService(): SourceConfigService {
        return SourceConfigServiceMockImpl()
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    open fun libraTransformationJmsListenerAnnBeanPostProcessor(sourceConfigService: SourceConfigService)
            : LibraTransformationJmsListenerAnnBeanPostProcessor {
        return LibraTransformationJmsListenerAnnBeanPostProcessor(sourceConfigService)
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
            tmpListenerYaml.containerFactory = CoreCommons.BeansConstants.INTERNAL_JMS_CONTAINER_FACTORY_NAME
            tmpListenerYaml.destination = it.transformationQueue
            tmpListenerYaml.messageListener = ConfigConstants.RAW_TRANS_MESSAGE_LISTENER
            tmpListenerYaml.dispatcher = ConfigConstants.TRANSFORMATION_PLATFORM
            list.add(tmpListenerYaml)
        }
        return list
    }

    override fun afterSingletonsInstantiated() {
    }
}
