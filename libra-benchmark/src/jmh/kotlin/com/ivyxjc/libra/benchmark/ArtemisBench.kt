package com.ivyxjc.libra.benchmark

import com.ivyxjc.libra.common.utils.getPrivateProperty
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.apache.activemq.artemis.jms.client.ActiveMQQueue
import org.apache.activemq.artemis.jms.client.ActiveMQSession
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import javax.jms.Connection
import javax.jms.MessageProducer
import javax.jms.Session


@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
open class ArtemisBench {
    private val url = getPrivateProperty("artemis.url")
    private val conFactory = ActiveMQConnectionFactory(url)
    private val connection: Connection = conFactory.createConnection()
    private val session = connection.createSession(true, Session.SESSION_TRANSACTED) as ActiveMQSession
    private val queue = ActiveMQQueue("IVY.TRANSMISSION12")
    private val mp: MessageProducer = session.createProducer(queue)

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


