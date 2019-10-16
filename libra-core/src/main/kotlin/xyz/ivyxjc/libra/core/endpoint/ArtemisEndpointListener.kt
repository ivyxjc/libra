package xyz.ivyxjc.libra.core.endpoint

import org.apache.activemq.artemis.jms.client.ActiveMQConnection
import org.apache.activemq.artemis.jms.client.ActiveMQMessageConsumer
import org.apache.activemq.artemis.jms.client.ActiveMQQueue
import xyz.ivyxjc.libra.common.utils.loggerFor
import javax.jms.Connection
import javax.jms.MessageConsumer
import javax.jms.Queue
import javax.jms.Session

class ArtemisEndpointListener : AbstractEndpointListener() {

    companion object {
        @JvmStatic
        private val log = loggerFor(ArtemisEndpointListener::class.java)
    }

    override fun setupMessageConsumer() {
        val queue = ActiveMQQueue(address)
        @Suppress("SENSELESS_COMPARISON")
        if (messageConsumer == null) {
            messageConsumer = createMessageConsumer(queue)
        }
        val mc = messageConsumer as ActiveMQMessageConsumer
        if (mc.isClosed) {
            messageConsumer = createMessageConsumer(queue)
        }
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


    private fun createConnection(): Connection {
        var retryCount = 0
        var res: Connection? = null
        var startFlag = false
        while (true) {
            try {
                if (retryCount > 5) {
                    log.error("fail to create jms connection, retry count {}", retryCount)
                }
                retryCount++
                res = jmsConnectionFactory.createConnection()
                res.start()
                Thread.sleep(1000)
                startFlag = true
                break
            } catch (e: Exception) {
                log.warn("fail to create jms connection, retry count {}", retryCount)
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
                    log.error("fail to create jms session, retry count {}", retryCount)
                    jmsConnection = createConnection()
                }
                retryCount++
                res = jmsConnection!!.createSession()
                Thread.sleep(1000)
                break
            } catch (e: Exception) {
                log.warn("fail to create jms session, retry count {}", retryCount)
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
                    log.error("fail to create jms message consumer, retry count {}", retryCount)
                    session = createSession()
                }
                retryCount++
                res = session!!.createConsumer(queue)
                Thread.sleep(1000)
                break
            } catch (e: Exception) {
                log.warn("fail to create jms message consumer, retry count {}", retryCount)
            }
        }
        return res
    }
}