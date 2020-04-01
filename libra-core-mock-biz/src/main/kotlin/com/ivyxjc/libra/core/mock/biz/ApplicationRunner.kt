package com.ivyxjc.libra.core.mock.biz

import com.ivyxjc.libra.core.CoreCommons
import com.ivyxjc.libra.core.CorePosition
import com.ivyxjc.libra.starter.platforms.transmission.EnableLibraTransmission
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.connection.CachingConnectionFactory
import org.springframework.jms.connection.JmsTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.jms.ConnectionFactory
import javax.sql.DataSource

@SpringBootApplication(scanBasePackageClasses = [CorePosition::class])
@PropertySource(value = ["private-endpoint.properties", "private-jdbc.properties"])
@MapperScan("com.ivyxjc.libra.core.dao")
@EnableTransactionManagement
@EnableLibraTransmission
open class ApplicationRunner


fun main() {
    SpringApplication.run(ApplicationRunner::class.java)
}

@Configuration
open class JmsConfig {

    @Autowired
    private lateinit var env: Environment

    @Bean(CoreCommons.BeansConstants.INTERNAL_JMS_CONNECTION_FACTORY_NAME)
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

    @Bean(CoreCommons.BeansConstants.INTERNAL_JMS_CONTAINER_FACTORY_NAME)
    open fun internalContainerFactory(
        internalConnectionFactory: ConnectionFactory
    ): DefaultJmsListenerContainerFactory {
        val containerFactory = DefaultJmsListenerContainerFactory()
        containerFactory.setConnectionFactory(internalConnectionFactory)
        containerFactory.setTransactionManager(jmsTransactionManager(internalConnectionFactory))
        return containerFactory
    }

    @Bean
    open fun dataSourceTransactionManager(dataSource: DataSource): DataSourceTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    private fun jmsTransactionManager(internalConnectionFactory: ConnectionFactory): JmsTransactionManager {
        return JmsTransactionManager(internalConnectionFactory)
    }
}
