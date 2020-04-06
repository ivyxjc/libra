package com.ivyxjc.libra.starter.jms

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory
import javax.jms.QueueConnectionFactory

class JmsConnectionUtils {
    companion object {
        @JvmStatic
        fun artemisConnectionFactory(url: String): QueueConnectionFactory {
            return ActiveMQQueueConnectionFactory(url)
        }

        @JvmStatic
        fun aqConnectionFactory(url: String): QueueConnectionFactory {
            return ActiveMQConnectionFactory(url)
        }
    }

}