package xyz.ivyxjc.libra.jms.core

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import xyz.ivyxjc.libra.common.utils.getProperty
import xyz.ivyxjc.libra.jms.connection.LibraCachingConnectionFactory
import javax.jms.DeliveryMode
import javax.jms.Session

private val artemisUrl = getProperty("artemis.url")


fun main() {
    val connectionFactory = ActiveMQConnectionFactory(artemisUrl)
    connectionFactory.isCacheDestinations = true
    val cacheConnectionFactory = LibraCachingConnectionFactory(connectionFactory)
    val libraJmsTemplate = LibraJmsTemplate(cacheConnectionFactory)
    libraJmsTemplate.sessionTransacted = false
    libraJmsTemplate.sessionAcknowledgeMode = Session.DUPS_OK_ACKNOWLEDGE
    libraJmsTemplate.deliveryMode = DeliveryMode.NON_PERSISTENT

    for (i in 0 until 1000) {
        println(i)
        libraJmsTemplate.convertAndSend("IVY.TRANS111", "hello")
    }
}
