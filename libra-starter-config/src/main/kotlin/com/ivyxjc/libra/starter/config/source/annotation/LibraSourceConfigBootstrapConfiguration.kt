package com.ivyxjc.libra.starter.config.source.annotation

import com.ivyxjc.libra.common.BeansConstants
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
open class LibraSourceConfigBootstrapConfiguration {

    @Bean(name = [BeansConstants.LIBRA_SOURCE_CONFIG_PROCESSOR_BEAN_NAME])
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    open fun libraSourceConfigAnnotationProcessor(): LibraSourceConfigAnnotationBeanPostProcessor {
        return LibraSourceConfigAnnotationBeanPostProcessor()
    }

}

