package com.ivyxjc.libra.starter.jms.annotation

import com.ivyxjc.libra.common.BeansConstants
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
open class LibraJmsBootstrapConfiguration {

    @Bean(name = [BeansConstants.LIBRA_JMS_LISTENER_PROCESSOR_BEAN_NAME])
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    open fun libraJmsListenerAnnotationProcessor(): LibraJmsListenerAnnotationBeanPostProcessor {
        return LibraJmsListenerAnnotationBeanPostProcessor()
    }

}

