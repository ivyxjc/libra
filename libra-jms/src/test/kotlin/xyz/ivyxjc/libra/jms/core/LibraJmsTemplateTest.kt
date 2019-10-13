package xyz.ivyxjc.libra.jms.core

import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory
import xyz.ivyxjc.libra.common.utils.getProperty
import xyz.ivyxjc.libra.jms.connection.LibraCachingConnectionFactory

private val artemisUrl = getProperty("artemis.url")


fun main() {
    val connectionFactory = ActiveMQQueueConnectionFactory(artemisUrl)
    val cacheConnectionFactory = LibraCachingConnectionFactory(connectionFactory)
    val libraJmsTemplate = LibraJmsTemplate(cacheConnectionFactory)
    libraJmsTemplate.sessionTransacted = true
    for (i in 0 until 100) {
        println(i)
        libraJmsTemplate.convertAndSend("IVY.TRANSMISSION6", "hello")
    }
}
