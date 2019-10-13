package xyz.ivyxjc.libra.benchmark

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.apache.activemq.artemis.jms.client.ActiveMQQueue
import org.apache.activemq.artemis.jms.client.ActiveMQSession
import org.openjdk.jmh.annotations.*
import xyz.ivyxjc.libra.common.utils.getProperty
import java.util.concurrent.TimeUnit
import javax.jms.Session


@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
open class ArtemisBench {
    val url = getProperty("artemis.url")
    val conFactory = ActiveMQConnectionFactory(url)
    val connection = conFactory.createConnection()
    val session = connection.createSession(true, Session.SESSION_TRANSACTED) as ActiveMQSession
    val queue = ActiveMQQueue("IVY.TRANSMISSION12")
    val mp = session.createProducer(queue)

    init {
        connection.start()
    }

    @Benchmark
    @Synchronized
    fun createConnection() {
        conFactory.createConnection()
    }

    @Benchmark
    @Synchronized
    fun createSession() {
        connection.createSession()
    }

    @Benchmark
    @Synchronized
    fun connectionStart() {
        connection.start()
    }

    @Benchmark
    @Synchronized
    fun sessionStart() {
        session.start()
    }

    @Benchmark
    @Synchronized
    fun sessionCommit() {
        val textMessage = session.createTextMessage()
        textMessage.text = "hello"
        mp.send(textMessage)
        session.commit()
    }
}


