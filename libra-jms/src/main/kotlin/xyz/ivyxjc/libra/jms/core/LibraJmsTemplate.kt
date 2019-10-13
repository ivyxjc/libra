package xyz.ivyxjc.libra.jms.core

import org.springframework.jms.JmsException
import org.springframework.jms.connection.ConnectionFactoryUtils
import org.springframework.jms.core.*
import org.springframework.jms.support.JmsUtils
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.SimpleMessageConverter
import org.springframework.lang.Nullable
import org.springframework.util.Assert
import xyz.ivyxjc.libra.common.utils.loggerFor
import javax.jms.*
import kotlin.IllegalStateException


class LibraJmsTemplate : JmsOperations {

    constructor() {
        this.messageConverter = SimpleMessageConverter()
    }

    constructor(connectionFactory: ConnectionFactory) {
        this.connectionFactory = connectionFactory
        this.messageConverter = SimpleMessageConverter()
    }


    companion object {
        @JvmStatic
        private val log = loggerFor(LibraJmsTemplate::class.java)
    }

    private var connectionFactory: ConnectionFactory? = null

    private var messageConverter: MessageConverter? = null

    var sessionTransacted = false

    var sessionAcknowledgeMode = Session.AUTO_ACKNOWLEDGE

    private var messageIdEnabled = true

    private var messageTimestampEnabled = true

    /**
     * check whether use publisher, subscriber mode
     * true: create topic connection
     * false: create queue connection
     */
    private var pubSubMode: Boolean = false

    override fun receiveSelected(messageSelector: String): Message? {
        TODO("not implemented")
    }

    override fun receiveSelected(destination: Destination, messageSelector: String): Message? {
        TODO("not implemented")
    }

    override fun receiveSelected(destinationName: String, messageSelector: String): Message? {
        TODO("not implemented")
    }

    override fun receiveAndConvert(): Any? {
        TODO("not implemented")
    }

    override fun receiveAndConvert(destination: Destination): Any? {
        TODO("not implemented")
    }

    override fun receiveAndConvert(destinationName: String): Any? {
        TODO("not implemented")
    }

    override fun sendAndReceive(messageCreator: MessageCreator): Message? {
        TODO("not implemented")
    }

    override fun sendAndReceive(destination: Destination, messageCreator: MessageCreator): Message? {
        TODO("not implemented")
    }

    override fun sendAndReceive(destinationName: String, messageCreator: MessageCreator): Message? {
        TODO("not implemented")
    }

    override fun <T : Any?> browse(action: BrowserCallback<T>): T? {
        TODO("not implemented")
    }

    override fun <T : Any?> browse(queue: Queue, action: BrowserCallback<T>): T? {
        TODO("not implemented")
    }

    override fun <T : Any?> browse(queueName: String, action: BrowserCallback<T>): T? {
        TODO("not implemented")
    }

    override fun <T : Any?> execute(action: SessionCallback<T>): T? {
        TODO("not implemented")
    }

    override fun <T : Any?> execute(action: ProducerCallback<T>): T? {
        TODO("not implemented")
    }

    override fun <T : Any?> execute(destination: Destination, action: ProducerCallback<T>): T? {
        TODO("not implemented")
    }

    override fun <T : Any?> execute(destinationName: String, action: ProducerCallback<T>): T? {
        TODO("not implemented")
    }

    override fun receiveSelectedAndConvert(messageSelector: String): Any? {
        TODO("not implemented")
    }

    override fun receiveSelectedAndConvert(destination: Destination, messageSelector: String): Any? {
        TODO("not implemented")
    }

    override fun receiveSelectedAndConvert(destinationName: String, messageSelector: String): Any? {
        TODO("not implemented")
    }

    override fun convertAndSend(message: Any) {
        TODO("not implemented")
    }

    override fun convertAndSend(destination: Destination, message: Any) {
        send(destination) { session -> getRequiredMessageConverter().toMessage(message, session) }
    }

    override fun convertAndSend(destinationName: String, message: Any) {
        send(destinationName) { session -> getRequiredMessageConverter().toMessage(message, session) }
    }

    override fun convertAndSend(message: Any, postProcessor: MessagePostProcessor) {
        TODO("not implemented")
    }

    override fun convertAndSend(destination: Destination, message: Any, postProcessor: MessagePostProcessor) {
        TODO("not implemented")
    }

    override fun convertAndSend(destinationName: String, message: Any, postProcessor: MessagePostProcessor) {
        TODO("not implemented")
    }

    override fun receive(): Message? {
        TODO("not implemented")
    }

    override fun receive(destination: Destination): Message? {
        TODO("not implemented")
    }

    override fun receive(destinationName: String): Message? {
        TODO("not implemented")
    }

    override fun send(messageCreator: MessageCreator) {
        TODO("not implemented")
    }

    override fun send(destination: Destination, messageCreator: MessageCreator) {
        execute(SessionCallback {
            doSend(it, destination, messageCreator)
            return@SessionCallback null
        }, false)
    }

    override fun send(destinationName: String, messageCreator: MessageCreator) {
        execute(SessionCallback {
            doSend(it, resolveDestination(it, destinationName), messageCreator)
            return@SessionCallback null
        }, false)
    }

    override fun <T : Any?> browseSelected(messageSelector: String, action: BrowserCallback<T>): T? {
        TODO("not implemented")
    }

    override fun <T : Any?> browseSelected(queue: Queue, messageSelector: String, action: BrowserCallback<T>): T? {
        TODO("not implemented")
    }

    override fun <T : Any?> browseSelected(queueName: String, messageSelector: String, action: BrowserCallback<T>): T? {
        TODO("not implemented")
    }

    @Throws(IllegalStateException::class)
    private fun getRequiredMessageConverter(): MessageConverter {
        return messageConverter
                ?: throw IllegalStateException("No 'messageConverter' specified. Check configuration of LibraJmsTemplate.")
    }

    @Throws(JMSException::class)
    private fun doSend(session: Session, destination: Destination, messageCreator: MessageCreator) {
        Assert.notNull(messageCreator, "MessageCreator must not be null")
        val producer = createProducer(session, destination)
        try {
            val message = messageCreator.createMessage(session)
            log.debug("Sending created message: {}", message)
            doSend(producer, message)
            // Check commit - avoid commit call within a JTA transaction.
            if (session.transacted) {
                // Transacted session created by this template -> commit.
                JmsUtils.commitIfNecessary(session)
            }
        } finally {
            JmsUtils.closeMessageProducer(producer)
        }

    }


    @Throws(JMSException::class)
    protected fun doSend(producer: MessageProducer, message: Message) {
        producer.send(message)
    }

    @Throws(JmsException::class)
    fun <T> execute(action: SessionCallback<T>, startConnection: Boolean): T? {
        var connToClose: Connection? = null
        var sessionToClose: Session? = null
        try {
            connToClose = obtainConnectionFactory().createConnection()
            sessionToClose = connToClose.createSession(sessionTransacted, sessionAcknowledgeMode)
            if (startConnection) {
                connToClose.start()
            }
            val sessionToUse = sessionToClose
            return action.doInJms(sessionToUse)
        } finally {
            JmsUtils.closeSession(sessionToClose)
            ConnectionFactoryUtils.releaseConnection(connToClose, connectionFactory, startConnection)
        }

    }

    private fun resolveDestination(session: Session, destinationName: String): Destination {
        return if (pubSubMode) {
            session.createTopic(destinationName) as Topic
        } else {
            session.createQueue(destinationName) as Queue
        }
    }

    protected fun obtainConnectionFactory(): ConnectionFactory {
        val cf = connectionFactory
        Assert.notNull(cf, "No ConnectionFactory set")
        return cf!!
    }


    @Throws(JMSException::class)
    private fun createProducer(session: Session, @Nullable destination: Destination?): MessageProducer {
        val producer = doCreateProducer(session, destination)
        if (!messageIdEnabled) {
            producer.setDisableMessageID(true)
        }
        if (!messageTimestampEnabled) {
            producer.setDisableMessageTimestamp(true)
        }
        return producer
    }

    @Throws(JMSException::class)
    protected fun doCreateProducer(session: Session, @Nullable destination: Destination?): MessageProducer {
        return session.createProducer(destination)
    }
}


