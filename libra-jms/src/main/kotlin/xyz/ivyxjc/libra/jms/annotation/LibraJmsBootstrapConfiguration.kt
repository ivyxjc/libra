package xyz.ivyxjc.libra.jms.annotation

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role
import xyz.ivyxjc.libra.common.JmsConstants

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
open class LibraJmsBootstrapConfiguration {

    @Bean(name = [JmsConstants.LIBRA_JMS_LISTENER_PROCESSOR_BEAN_NAME])
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    open fun libraJmsListenerAnnotationProcessor(): LibraJmsListenerAnnotationBeanPostProcessor {
        return LibraJmsListenerAnnotationBeanPostProcessor()
    }

}

