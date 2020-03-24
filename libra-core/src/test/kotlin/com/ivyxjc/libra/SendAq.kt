package com.ivyxjc.libra

import com.ivyxjc.libra.common.utils.getProperty
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.apache.activemq.artemis.jms.client.ActiveMQQueue
import org.apache.activemq.artemis.jms.client.ActiveMQSession
import javax.jms.DeliveryMode
import javax.jms.Session

private const val threadNum = 12
private const val count = 200

private val artemisUrl = getProperty("artemis.url")
private val aqUrl = getProperty("aq.url")

fun sendArtemis() {
    val url = getProperty("artemis.url")
    val connectionFactory = ActiveMQConnectionFactory(artemisUrl)
    val connect = connectionFactory.createConnection()
    connect.start()

    val queue = ActiveMQQueue("IVY.TRANSMISSION1")
    val arr = Array(threadNum) {
        Thread(Runnable {
            val session = connect.createSession() as ActiveMQSession
            session.start()
            val mp = session.createProducer(queue)
            mp.deliveryMode = DeliveryMode.NON_PERSISTENT
            for (i in 0..count) {
                val t1 = System.currentTimeMillis()
                val bytesMsg = session.createBytesMessage()
                bytesMsg.writeBytes(buildRawTransBytes())
                mp.send(bytesMsg)
                val t2 = System.currentTimeMillis()
                println("+++++++++++${t2 - t1}+++++++++++++++")
            }
        })
    }
    arr.forEach { it.start() }
}

fun sendAq() {
    val connectionFactory = org.apache.activemq.ActiveMQConnectionFactory(aqUrl)


    val queue = org.apache.activemq.command.ActiveMQQueue("IVY.TRANSMISSION1")
    val arr = Array(threadNum) {
        Thread(Runnable {
            val connect = connectionFactory.createConnection()
            connect.start()
            val session = connect.createSession(false, Session.AUTO_ACKNOWLEDGE)
            val mp = session.createProducer(queue)
            mp.deliveryMode = DeliveryMode.NON_PERSISTENT
            for (i in 0..count) {
                val t1 = System.currentTimeMillis()
                val bytesMsg = session.createBytesMessage()
                bytesMsg.writeBytes(buildRawTransBytes())
                mp.send(bytesMsg)
                val t2 = System.currentTimeMillis()
                println("+++++++++++${t2 - t1}+++++++++++++++")
            }
        })
    }
    arr.forEach { it.start() }
}


suspend fun main() = coroutineScope {
    //    launch {
//        sendAq()
//    }
    launch {
        sendArtemis()
    }
    Thread.sleep(1000000)
    println("hello world")
}

