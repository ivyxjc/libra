package com.ivyxjc.libra.starter.config.usecases.annotation

import com.ivyxjc.libra.core.expose.BeansConstants
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
open class LibraUsecaseConfigBootstrapConfiguration {

    @Bean(name = [BeansConstants.LIBRA_USECASE_CONFIG_PROCESSOR_BEAN_NAME])
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    open fun libraUsecaseConfigAnnotationProcessor(): LibraUsecaseConfigAnnotationBeanPostProcessor {
        return LibraUsecaseConfigAnnotationBeanPostProcessor()
    }
}
