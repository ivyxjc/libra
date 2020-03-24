package xyz.ivyxjc.libra.starter.jms.deprecated.connection

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.apache.activemq.artemis.jms.client.ActiveMQQueue
import org.junit.Test
import xyz.ivyxjc.libra.common.utils.getProperty
import xyz.ivyxjc.libra.jms.connection.LibraCachingConnectionFactory
import javax.jms.Connection
import javax.jms.Session

private val artemisUrl = getProperty("artemis.url")

class LibraCachingConnectionFactoryTest {

    @Test
    fun doTest1() {
        val connectionFactory = ActiveMQConnectionFactory(artemisUrl)
        val cf = LibraCachingConnectionFactory(connectionFactory)
        val list = mutableListOf<Connection>()
        val sessionList = mutableListOf<Session>()
        Thread(Runnable {
            list.add(cf.createConnection())
        }).start()
        Thread(Runnable {
            list.add(cf.createConnection())
        }).start()
        Thread(Runnable {
            list.add(cf.createConnection())
        }).start()
        Thread(Runnable {
            list.add(cf.createConnection())
        }).start()
        Thread(Runnable {
            list.add(cf.createConnection())
        }).start()
        Thread(Runnable {
            list.add(cf.createConnection())
        }).start()
        Thread(Runnable {
            list.add(cf.createConnection())
        }).start()
        Thread(Runnable {
            list.add(cf.createConnection())
        }).start()
        Thread(Runnable {
            list.add(cf.createConnection())
        }).start()
        Thread.sleep(4000)
        list.forEach {
            val session = it.createSession()
            sessionList.add(session)
            session.close()
        }
        sessionList.forEach {
            println(it)
        }
        val queue = ActiveMQQueue("DESTINATION")
        val mp = sessionList[0].createProducer(queue)
        println(mp)
        val mp2 = sessionList[0].createProducer(queue)
        println(mp2)
    }

}