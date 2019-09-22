package xyz.ivyxjc.libra.core.connection

import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory
import javax.jms.QueueConnectionFactory

class JmsConnectionUtils {
    companion object {
        @JvmStatic
        fun aqConnectionFactory(url: String): QueueConnectionFactory {
            return ActiveMQQueueConnectionFactory(url)
        }
    }

}