package com.ivyxjc.libra.starter.config.source.annotation

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role
import xyz.ivyxjc.libra.common.BeansConstants

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
open class LibraSourceConfigBootstrapConfiguration {

    @Bean(name = [BeansConstants.LIBRA_SOURCE_CONFIG_PROCESSOR_BEAN_NAME])
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    open fun libraSourceConfigAnnotationProcessor(): LibraSourceConfigAnnotationBeanPostProcessor {
        return LibraSourceConfigAnnotationBeanPostProcessor()
    }

}

