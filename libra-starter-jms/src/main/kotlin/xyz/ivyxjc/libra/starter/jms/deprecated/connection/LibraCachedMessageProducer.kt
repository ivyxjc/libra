package xyz.ivyxjc.libra.starter.jms.deprecated.connection

import org.springframework.lang.Nullable
import javax.jms.*


/**
 * copy from CachedMessageProducer
 *
 * @see org.springframework.jms.connection.CachedMessageProducer
 */
internal class LibraCachedMessageProducer @Throws(JMSException::class)
constructor(private val target: MessageProducer) : MessageProducer, QueueSender, TopicPublisher {

    @Nullable
    private var originalDisableMessageID: Boolean? = null

    @Nullable
    private var originalDisableMessageTimestamp: Boolean? = null

    @Nullable
    private var originalDeliveryDelay: Long? = null

    private var deliveryMode: Int = 0

    private var priority: Int = 0

    private var timeToLive: Long = 0


    init {
        this.deliveryMode = target.deliveryMode
        this.priority = target.priority
        this.timeToLive = target.timeToLive
    }


    @Throws(JMSException::class)
    override fun setDisableMessageID(disableMessageID: Boolean) {
        if (this.originalDisableMessageID == null) {
            this.originalDisableMessageID = this.target.disableMessageID
        }
        this.target.disableMessageID = disableMessageID
    }

    @Throws(JMSException::class)
    override fun getDisableMessageID(): Boolean {
        return this.target.disableMessageID
    }

    @Throws(JMSException::class)
    override fun setDisableMessageTimestamp(disableMessageTimestamp: Boolean) {
        if (this.originalDisableMessageTimestamp == null) {
            this.originalDisableMessageTimestamp = this.target.disableMessageTimestamp
        }
        this.target.disableMessageTimestamp = disableMessageTimestamp
    }

    @Throws(JMSException::class)
    override fun getDisableMessageTimestamp(): Boolean {
        return this.target.disableMessageTimestamp
    }

    @Throws(JMSException::class)
    override fun setDeliveryDelay(deliveryDelay: Long) {
        if (this.originalDeliveryDelay == null) {
            this.originalDeliveryDelay = this.target.deliveryDelay
        }
        this.target.deliveryDelay = deliveryDelay
    }

    @Throws(JMSException::class)
    override fun getDeliveryDelay(): Long {
        return this.target.deliveryDelay
    }

    override fun setDeliveryMode(deliveryMode: Int) {
        this.deliveryMode = deliveryMode
    }

    override fun getDeliveryMode(): Int {
        return this.deliveryMode
    }

    override fun setPriority(priority: Int) {
        this.priority = priority
    }

    override fun getPriority(): Int {
        return this.priority
    }

    override fun setTimeToLive(timeToLive: Long) {
        this.timeToLive = timeToLive
    }

    override fun getTimeToLive(): Long {
        return this.timeToLive
    }

    @Throws(JMSException::class)
    override fun getDestination(): Destination {
        return this.target.destination
    }

    @Throws(JMSException::class)
    override fun getQueue(): Queue {
        return this.target.destination as Queue
    }

    @Throws(JMSException::class)
    override fun getTopic(): Topic {
        return this.target.destination as Topic
    }

    @Throws(JMSException::class)
    override fun send(message: Message) {
        this.target.send(message, this.deliveryMode, this.priority, this.timeToLive)
    }

    @Throws(JMSException::class)
    override fun send(message: Message, deliveryMode: Int, priority: Int, timeToLive: Long) {
        this.target.send(message, deliveryMode, priority, timeToLive)
    }

    @Throws(JMSException::class)
    override fun send(destination: Destination, message: Message) {
        this.target.send(destination, message, this.deliveryMode, this.priority, this.timeToLive)
    }

    @Throws(JMSException::class)
    override fun send(destination: Destination, message: Message, deliveryMode: Int, priority: Int, timeToLive: Long) {
        this.target.send(destination, message, deliveryMode, priority, timeToLive)
    }

    @Throws(JMSException::class)
    override fun send(message: Message, completionListener: CompletionListener) {
        this.target.send(message, this.deliveryMode, this.priority, this.timeToLive, completionListener)
    }

    @Throws(JMSException::class)
    override fun send(message: Message, deliveryMode: Int, priority: Int, timeToLive: Long,
                      completionListener: CompletionListener) {

        this.target.send(message, deliveryMode, priority, timeToLive, completionListener)
    }

    @Throws(JMSException::class)
    override fun send(destination: Destination, message: Message, completionListener: CompletionListener) {
        this.target.send(destination, message, this.deliveryMode, this.priority, this.timeToLive, completionListener)
    }

    @Throws(JMSException::class)
    override fun send(destination: Destination, message: Message, deliveryMode: Int, priority: Int,
                      timeToLive: Long, completionListener: CompletionListener) {

        this.target.send(destination, message, deliveryMode, priority, timeToLive, completionListener)

    }

    @Throws(JMSException::class)
    override fun send(queue: Queue, message: Message) {
        this.target.send(queue, message, this.deliveryMode, this.priority, this.timeToLive)
    }

    @Throws(JMSException::class)
    override fun send(queue: Queue, message: Message, deliveryMode: Int, priority: Int, timeToLive: Long) {
        this.target.send(queue, message, deliveryMode, priority, timeToLive)
    }

    @Throws(JMSException::class)
    override fun publish(message: Message) {
        this.target.send(message, this.deliveryMode, this.priority, this.timeToLive)
    }

    @Throws(JMSException::class)
    override fun publish(message: Message, deliveryMode: Int, priority: Int, timeToLive: Long) {
        this.target.send(message, deliveryMode, priority, timeToLive)
    }

    @Throws(JMSException::class)
    override fun publish(topic: Topic, message: Message) {
        this.target.send(topic, message, this.deliveryMode, this.priority, this.timeToLive)
    }

    @Throws(JMSException::class)
    override fun publish(topic: Topic, message: Message, deliveryMode: Int, priority: Int, timeToLive: Long) {
        this.target.send(topic, message, deliveryMode, priority, timeToLive)
    }

    @Throws(JMSException::class)
    override fun close() {
        // It's a cached MessageProducer... reset properties only.
        if (this.originalDisableMessageID != null) {
            this.target.disableMessageID = this.originalDisableMessageID!!
            this.originalDisableMessageID = null
        }
        if (this.originalDisableMessageTimestamp != null) {
            this.target.disableMessageTimestamp = this.originalDisableMessageTimestamp!!
            this.originalDisableMessageTimestamp = null
        }
        if (this.originalDeliveryDelay != null) {
            this.target.deliveryDelay = this.originalDeliveryDelay!!
            this.originalDeliveryDelay = null
        }
    }

    override fun toString(): String {
        return "Cached JMS MessageProducer: " + this.target
    }

}
