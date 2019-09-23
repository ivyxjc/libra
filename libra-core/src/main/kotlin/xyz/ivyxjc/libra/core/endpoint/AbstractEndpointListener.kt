package xyz.ivyxjc.libra.core.endpoint


import xyz.ivyxjc.libra.core.loggerFor
import javax.jms.*

abstract class AbstractEndpointListener {
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

    fun start() {
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

