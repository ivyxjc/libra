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
import xyz.ivyxjc.libra.core.StartupInit
import xyz.ivyxjc.libra.jms.connection.LibraCachingConnectionFactory
import xyz.ivyxjc.libra.jms.core.LibraJmsTemplate
import javax.jms.ConnectionFactory
import javax.jms.DeliveryMode
import javax.jms.Session

@SpringBootApplication(exclude = [JmsAutoConfiguration::class], scanBasePackageClasses = [StartupInit::class])
@PropertySource(value = ["private-endpoint.properties", "private-jdbc.properties"])
@MapperScan("xyz.ivyxjc.libra.core.dao")
open class ApplicationRunner


fun main() {
    SpringApplication.run(ApplicationRunner::class.java)
    Thread.sleep(1000000)
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
        return LibraCachingConnectionFactory(connectionFactory)
    }

    @Bean
    open fun jmsTemplate(internalConnectionFactory: ConnectionFactory): LibraJmsTemplate {
        val jmsTemplate = LibraJmsTemplate(internalConnectionFactory)
        jmsTemplate.sessionTransacted = false
        jmsTemplate.sessionAcknowledgeMode = Session.DUPS_OK_ACKNOWLEDGE
        jmsTemplate.deliveryMode = DeliveryMode.NON_PERSISTENT
        return jmsTemplate
    }
}
