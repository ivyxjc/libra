package xyz.ivyxjc.libra.jms.spring

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.springframework.jms.connection.CachingConnectionFactory
import org.springframework.jms.core.JmsTemplate
import xyz.ivyxjc.libra.common.utils.getProperty
import javax.jms.DeliveryMode
import javax.jms.Session


private val artemisUrl = getProperty("artemis.url")

fun main() {
    val connectionFactory = ActiveMQConnectionFactory(artemisUrl)
    val cacheConnectionFactory = CachingConnectionFactory(connectionFactory)
    val jmsTemplate = JmsTemplate(cacheConnectionFactory)
    jmsTemplate.isExplicitQosEnabled = true
    jmsTemplate.isSessionTransacted = false
    jmsTemplate.sessionAcknowledgeMode = Session.CLIENT_ACKNOWLEDGE
    jmsTemplate.deliveryMode = DeliveryMode.NON_PERSISTENT

    val t1 = System.currentTimeMillis()
    for (i in 0 until 1000) {
        Thread.sleep(1000)
        val tt1 = System.currentTimeMillis()
        jmsTemplate.convertAndSend("IVY.TRANS111", "hello")
        val tt2 = System.currentTimeMillis()
        println(tt2 - tt1)
//    jmsTemplate.convertAndSend(queue, "hello")
    }
    val t2 = System.currentTimeMillis()
    println("=========${t2 - t1}")
}

