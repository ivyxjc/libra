package com.ivyxjc.libra.starter.jms.pure

import com.ivyxjc.libra.common.utils.getProperty
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.apache.activemq.artemis.jms.client.ActiveMQQueue
import javax.jms.DeliveryMode
import javax.jms.Session

private val artemisUrl = getProperty("artemis.url")
fun main() {
    val connectionFactory = ActiveMQConnectionFactory(artemisUrl)
    val connect = connectionFactory.createConnection()
    connect.start()
    val queue = ActiveMQQueue("IVY.TRANSMISSION11")

    val session = connect.createSession(false, Session.DUPS_OK_ACKNOWLEDGE)
//    val session = connect.createSession()

    val mp = session.createProducer(queue)
    mp.deliveryMode = DeliveryMode.NON_PERSISTENT

    for (i in 0..5000) {
        println(i)
        val textMessage = session.createTextMessage()
        textMessage.text = "hello"
        mp.send(textMessage)
//        session.commit()
    }
//    session.commit()

}