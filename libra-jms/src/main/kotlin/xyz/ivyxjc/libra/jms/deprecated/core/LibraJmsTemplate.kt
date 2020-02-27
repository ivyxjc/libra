package xyz.ivyxjc.libra.jms.deprecated.core

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

    var deliveryMode = DeliveryMode.NON_PERSISTENT

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
        log.trace("start to create producer")
        val producer = createProducer(session, destination)
        log.trace("success to create producer: {}", producer)
        try {
            val message = messageCreator.createMessage(session)
            log.debug("Sending created message: {}", message)
            doSend(producer, message)
            // Check commit - avoid commit call within a JTA transaction.
            if (session.transacted) {
                // Transacted session created by this template -> commit.
                log.trace("start to commit session")
                JmsUtils.commitIfNecessary(session)
                log.trace("success to commit session")
            }
        } finally {
            log.trace("start to close message producer")
            JmsUtils.closeMessageProducer(producer)
            log.trace("success to close message producer")
        }

    }


    @Throws(JMSException::class)
    private fun doSend(producer: MessageProducer, message: Message) {
        producer.send(message)
    }

    @Throws(JmsException::class)
    fun <T> execute(action: SessionCallback<T>, startConnection: Boolean): T? {
        var connToClose: Connection? = null
        var sessionToClose: Session? = null
        try {
            log.trace("start to get connection")
            connToClose = obtainConnectionFactory().createConnection()
            log.trace("success to get connection, start to create session")
            sessionToClose = connToClose.createSession(sessionTransacted, sessionAcknowledgeMode)
            log.trace("success to create session")
            if (startConnection) {
                connToClose.start()
            }
            val sessionToUse = sessionToClose
            log.trace("start to doInJms")
            return action.doInJms(sessionToUse)
        } finally {
            log.trace("start to close session")
            JmsUtils.closeSession(sessionToClose)
            log.trace("success to close session and start to release connection")
            ConnectionFactoryUtils.releaseConnection(connToClose, connectionFactory, startConnection)
            log.trace("success to release connection")
        }

    }

    private fun resolveDestination(session: Session, destinationName: String): Destination {
        return if (pubSubMode) {
            session.createTopic(destinationName) as Topic
        } else {
            session.createQueue(destinationName) as Queue
        }
    }

    private fun obtainConnectionFactory(): ConnectionFactory {
        val cf = connectionFactory
        Assert.notNull(cf, "No ConnectionFactory set")
        return cf!!
    }


    @Throws(JMSException::class)
    private fun createProducer(session: Session, @Nullable destination: Destination?): MessageProducer {
        val producer = doCreateProducer(session, destination)
        if (!messageIdEnabled) {
            producer.disableMessageID = true
        }
        if (!messageTimestampEnabled) {
            producer.disableMessageTimestamp = true
        }
        producer.deliveryMode = this.deliveryMode
        return producer
    }

    @Throws(JMSException::class)
    private fun doCreateProducer(session: Session, @Nullable destination: Destination?): MessageProducer {
        return session.createProducer(destination)
    }
}


