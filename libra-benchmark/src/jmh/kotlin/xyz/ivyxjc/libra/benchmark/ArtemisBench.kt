package xyz.ivyxjc.libra.benchmark

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.apache.activemq.artemis.jms.client.ActiveMQSession
import org.openjdk.jmh.annotations.*
import xyz.ivyxjc.libra.common.utils.getProperty
import java.util.concurrent.TimeUnit


@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
open class ArtemisBench {
    val url = getProperty("artemis.url")
    val conFactory = ActiveMQConnectionFactory(url)
    val connection = conFactory.createConnection()
    val session = connection.createSession() as ActiveMQSession

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
}


