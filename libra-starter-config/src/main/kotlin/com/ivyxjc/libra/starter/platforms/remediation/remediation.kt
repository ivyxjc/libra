package com.ivyxjc.libra.starter.platforms.remediation

import com.ivyxjc.libra.core.CoreCommons
import com.ivyxjc.libra.core.platforms.RemediationPlatform
import com.ivyxjc.libra.core.service.UsecaseConfigService
import com.ivyxjc.libra.core.service.UsecaseConfigServiceMockImpl
import com.ivyxjc.libra.starter.common.model.LibraJmsListenerYaml
import com.ivyxjc.libra.starter.common.processors.AbstractLibraJmsAnnBeanPostProcessor
import com.ivyxjc.libra.starter.config.usecases.annotation.EnableLibraUsecaseConfig
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
@Import(LibraremediationBootstrapConfiguration::class)
@EnableJms
@EnableLibraUsecaseConfig
annotation class EnableLibraRemediation


@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
open class LibraremediationBootstrapConfiguration {

    @Bean
    open fun remediationPlatform(usecaseConfigService: UsecaseConfigService): RemediationPlatform {
        return RemediationPlatform(usecaseConfigService)
    }

    @Bean
    open fun usecaseConfigService(): UsecaseConfigService {
        return UsecaseConfigServiceMockImpl()
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    open fun libraRemediationJmsListenerAnnBeanPostProcessor(useCaseConfigService: UsecaseConfigService)
            : LibraRemediationJmsListenerAnnBeanPostProcessor {
        return LibraRemediationJmsListenerAnnBeanPostProcessor(useCaseConfigService)
    }
}


class LibraRemediationJmsListenerAnnBeanPostProcessor(val useCaseConfigService: UsecaseConfigService) :
    AbstractLibraJmsAnnBeanPostProcessor(ConfigConstants.REMEDIATION_JMS_NAME) {

    override fun processJmsListenerConfig(): List<LibraJmsListenerYaml> {
        val list = mutableListOf<LibraJmsListenerYaml>()
        val usecaseConfigs = useCaseConfigService.listAppUsecases()
        usecaseConfigs.forEach {
            val tmpListenerYaml = LibraJmsListenerYaml()
            tmpListenerYaml.id = it.name
            tmpListenerYaml.containerFactory = CoreCommons.BeansConstants.INTERNAL_JMS_CONTAINER_FACTORY_NAME
            tmpListenerYaml.destination = it.queue
            tmpListenerYaml.messageListener = ConfigConstants.RAW_TRANS_MESSAGE_LISTENER
            tmpListenerYaml.dispatcher = ConfigConstants.REMEDIATION_PLATFORM
            list.add(tmpListenerYaml)
        }
        return list
    }

    override fun afterSingletonsInstantiated() {
    }
}