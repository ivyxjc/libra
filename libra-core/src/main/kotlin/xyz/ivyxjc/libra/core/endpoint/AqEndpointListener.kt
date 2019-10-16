package xyz.ivyxjc.libra.core.endpoint

import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.command.ActiveMQQueue
import xyz.ivyxjc.libra.common.utils.loggerFor
import javax.jms.Connection
import javax.jms.MessageConsumer
import javax.jms.Queue
import javax.jms.Session

class AqEndpointListener : AbstractEndpointListener() {

    companion object {
        @JvmStatic
        private val log = loggerFor(AqEndpointListener::class.java)
    }

    override fun setupSession() {
        @Suppress("SENSELESS_COMPARISON")
        if (session == null) {
            session = createSession()
        }
    }

    override fun setupConnection() {
        @Suppress("SENSELESS_COMPARISON")
        if (jmsConnection == null) {
            jmsConnection = createConnection()
        }
        val con = jmsConnection as ActiveMQConnection
        if (!con.isStarted) {
            jmsConnection = createConnection()
        }
    }

    override fun setupMessageConsumer() {
        val queue = ActiveMQQueue(address)
        @Suppress("SENSELESS_COMPARISON")
        if (messageConsumer == null) {
            messageConsumer = createMessageConsumer(queue)
        }
    }

    private fun createConnection(): Connection {
        var retryCount = 0
        var res: Connection? = null
        var startFlag = false
        while (true) {
            try {
                if (retryCount > 5) {
                    log.error("fail to create jms connection, retry count $retryCount")
                }
                retryCount++
                res = jmsConnectionFactory.createConnection()
                res.start()
                Thread.sleep(1000)
                startFlag = true
                break
            } catch (e: Exception) {
                log.warn("fail to create jms connection, retry count $retryCount")
                if (!startFlag) {
                    if (res != null) {
                        try {
                            res.close()
                        } catch (e: Exception) {
                            log.error("fail to close jms connection")
                        }
                    }
                }
            }
        }
        return res!!
    }

    private fun createSession(): Session {
        var retryCount = 0
        var res: Session
        while (true) {
            try {
                if (retryCount > 5) {
                    log.error("fail to create jms session, retry count $retryCount")
                    jmsConnection = createConnection()
                }
                retryCount++
                res = jmsConnection!!.createSession(false, Session.AUTO_ACKNOWLEDGE)
                Thread.sleep(1000)
                break
            } catch (e: Exception) {
                log.warn("fail to create jms session, retry count $retryCount")
            }
        }
        return res
    }

    private fun createMessageConsumer(queue: Queue): MessageConsumer {
        var retryCount = 0
        var res: MessageConsumer
        while (true) {
            try {
                if (retryCount > 5) {
                    log.error("fail to create jms message consumer, retry count $retryCount")
                    session = createSession()
                }
                retryCount++
                res = session!!.createConsumer(queue)
                Thread.sleep(1000)
                break
            } catch (e: Exception) {
                log.warn("fail to create jms message consumer, retry count $retryCount")
            }
        }
        return res
    }
}