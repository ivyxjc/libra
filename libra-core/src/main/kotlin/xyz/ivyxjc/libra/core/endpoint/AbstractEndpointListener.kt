package xyz.ivyxjc.libra.core.endpoint


import org.springframework.beans.factory.InitializingBean
import xyz.ivyxjc.libra.common.utils.loggerFor
import javax.jms.*


interface EndpointListener {
    fun start()
}

/**
 * for the reason that you cannot access the same jms session in multiple threads
 * So the AbstractEndpointListener should be one listener one thread
 */
abstract class AbstractEndpointListener : EndpointListener, InitializingBean {
    companion object {
        @JvmStatic
        private val log = loggerFor(AbstractEndpointListener::class.java)
    }

    lateinit var jmsConnectionFactory: QueueConnectionFactory

    lateinit var messageListener: MessageListener

    lateinit var address: String

    var listenerCount: Int = 1

    protected var jmsConnection: Connection? = null

    protected var session: Session? = null

    protected var messageConsumer: MessageConsumer? = null

    override fun afterPropertiesSet() {
        start()
    }

    override fun start() {
        setupConnection()
        setupSession()
        setupMessageConsumer()
        messageConsumer!!.messageListener = messageListener
        log.info("thread running")
    }

    internal abstract fun setupSession()
    internal abstract fun setupConnection()
    internal abstract fun setupMessageConsumer()
}

