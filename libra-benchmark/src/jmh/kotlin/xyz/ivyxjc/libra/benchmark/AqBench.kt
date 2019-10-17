package xyz.ivyxjc.libra.benchmark

import org.apache.activemq.ActiveMQConnectionFactory
import org.openjdk.jmh.annotations.*
import xyz.ivyxjc.libra.common.utils.getProperty
import java.util.concurrent.TimeUnit
import javax.jms.Connection
import javax.jms.Session

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
open class AqBench {
    private val url = getProperty("aq.url")
    private val conFactory = ActiveMQConnectionFactory(url)
    private val connection: Connection = conFactory.createConnection()

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
        connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    }

    @Benchmark
    @Synchronized
    fun connectionStart() {
        connection.start()
    }
}