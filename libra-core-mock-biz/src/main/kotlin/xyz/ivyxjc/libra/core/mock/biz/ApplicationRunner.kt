package xyz.ivyxjc.libra.core.mock.biz

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.connection.CachingConnectionFactory
import org.springframework.jms.core.JmsTemplate
import xyz.ivyxjc.libra.core.CorePosition
import xyz.ivyxjc.libra.jms.annotation.EnableLibraJms
import javax.jms.ConnectionFactory
import javax.jms.DeliveryMode
import javax.jms.Session

@SpringBootApplication(exclude = [JmsAutoConfiguration::class], scanBasePackageClasses = [CorePosition::class])
@PropertySource(value = ["private-endpoint.properties", "private-jdbc.properties"])
@MapperScan("xyz.ivyxjc.libra.core.dao")
@EnableLibraJms
open class ApplicationRunner


fun main() {
    SpringApplication.run(ApplicationRunner::class.java)
}

@Configuration
open class JmsConfig {

    @Autowired
    private lateinit var env: Environment

    @Bean
    open fun internalConnectionFactory(): ConnectionFactory {
        val url = env["libra.internal.mq.url"]
        val connectionFactory = ActiveMQConnectionFactory(url)
        connectionFactory.isCacheDestinations = true
        val cachingConnectionFactory = CachingConnectionFactory(connectionFactory)
        cachingConnectionFactory.isCacheProducers = true
        cachingConnectionFactory.isCacheConsumers = true
        cachingConnectionFactory.sessionCacheSize = 20
        return cachingConnectionFactory
    }

    @Bean
    open fun internalContainerFactory(internalConnectionFactory: ConnectionFactory): DefaultJmsListenerContainerFactory {
        val containerFactory = DefaultJmsListenerContainerFactory()
        containerFactory.setConnectionFactory(internalConnectionFactory)
        return containerFactory
    }

    @Bean
    open fun jmsTemplate(internalConnectionFactory: ConnectionFactory): JmsTemplate {
        val jmsTemplate = JmsTemplate(internalConnectionFactory)
        jmsTemplate.sessionAcknowledgeMode = Session.DUPS_OK_ACKNOWLEDGE
        jmsTemplate.deliveryMode = DeliveryMode.NON_PERSISTENT
        return jmsTemplate
    }
}
