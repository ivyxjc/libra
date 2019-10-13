package xyz.ivyxjc.libra.jms.spring

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.apache.activemq.artemis.jms.client.ActiveMQQueue
import org.springframework.jms.connection.CachingConnectionFactory
import org.springframework.jms.core.JmsTemplate
import xyz.ivyxjc.libra.common.utils.getProperty
import javax.jms.DeliveryMode


private val artemisUrl = getProperty("artemis.url")

fun main() {
    val connectionFactory = ActiveMQConnectionFactory(artemisUrl)
    val cacheConnectionFactory = CachingConnectionFactory(connectionFactory)
    cacheConnectionFactory.sessionCacheSize = 20
    cacheConnectionFactory
    val jmsTemplate = JmsTemplate(cacheConnectionFactory)
    jmsTemplate.isSessionTransacted = true
    jmsTemplate.deliveryMode = DeliveryMode.NON_PERSISTENT

    val queue = ActiveMQQueue("IVY.TRANSMISSION3")
    val t1 = System.currentTimeMillis()
    for (i in 0 until 10000) {
        println(i)
        jmsTemplate.convertAndSend("IVY.TRANSM", "hello")
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Thread.sleep(100)
//        }
    }
    val t2 = System.currentTimeMillis()
    println("=========${t2 - t1}")
}