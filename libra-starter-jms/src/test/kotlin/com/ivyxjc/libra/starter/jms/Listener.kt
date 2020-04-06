package com.ivyxjc.libra.starter.jms

import com.ivyxjc.libra.common.utils.getProperty
import com.ivyxjc.libra.core.endpoint.RawTransactionMessageListener
import com.ivyxjc.libra.core.endpoint.UsecaseTxnMessageListener
import com.ivyxjc.libra.core.platforms.BlankRawTransDispatcher
import com.ivyxjc.libra.core.platforms.BlankUcTxnDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms.MessageListener
import javax.jms.Session

private val artemisUrl = getProperty("artemis.url")
private val aqUrl = getProperty("aq.url")

fun artemisListener(ml: MessageListener) {
    val listener1 = ArtemisEndpointListener()
    listener1.jmsConnectionFactory = JmsConnectionUtils.artemisConnectionFactory(artemisUrl)
    listener1.address = "IVY.TRANSMISSION1"
    listener1.messageListener = ml
    listener1.start()
    Thread.sleep(100000)
}


fun artemisListenerPure(ml: MessageListener) {
    val connectionFactory = ActiveMQConnectionFactory(artemisUrl)
    val connect = connectionFactory.createConnection()
    val session = connect.createSession()
    val queue = session.createQueue("IVY.TRANSMISSION1")
    val consumer = session.createConsumer(queue)
    consumer.messageListener = ml
    connect.start()
    Thread.sleep(100000)
}


fun aqListener(ml: MessageListener) {
    val listener1 = AqEndpointListener()
    listener1.jmsConnectionFactory = JmsConnectionUtils.aqConnectionFactory(aqUrl)
    listener1.address = "IVY.TRANSMISSION1"
    listener1.messageListener = ml
    listener1.start()
    Thread.sleep(100000)
}

fun aqListenerPure(ml: MessageListener) {
    val connectionFactory = org.apache.activemq.ActiveMQConnectionFactory(aqUrl)
    val connect = connectionFactory.createConnection()
    val session = connect.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val queue = session.createQueue("IVY.TRANSMISSION1")
    val consumer = session.createConsumer(queue)
    consumer.messageListener = ml
    connect.start()
    Thread.sleep(100000)
}


suspend fun main() = coroutineScope {
    val rawTansMl = RawTransactionMessageListener()
    val ucTxnMl = UsecaseTxnMessageListener()
    rawTansMl.dispatcher = BlankRawTransDispatcher()
    ucTxnMl.dispatcher = BlankUcTxnDispatcher()
    launch {
        artemisListener(rawTansMl)
    }
    launch {
        artemisListenerPure(rawTansMl)
    }
    launch {
        aqListener(rawTansMl)
    }
    launch {
        aqListenerPure(rawTansMl)
    }
    println("Hello")
}
