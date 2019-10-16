package xyz.ivyxjc.libra.jms.connection

import org.springframework.jms.JmsException
import org.springframework.lang.Nullable
import org.springframework.util.Assert
import org.springframework.util.ObjectUtils
import xyz.ivyxjc.libra.common.utils.loggerFor
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.jms.*
import javax.management.JMException
import kotlin.IllegalStateException


/**
 * This class is like CachingConnectionFactory in spring-jms
 *
 * @see org.springframework.jms.connection.CachingConnectionFactory
 */
class LibraCachingConnectionFactory : ConnectionFactory {

    companion object {
        @JvmStatic
        private val log = loggerFor(LibraCachingConnectionFactory::class.java)
    }

    var targetConnectionFactory: ConnectionFactory? = null

    private var sessionCacheSize: Int = 1
    private var connection: Connection? = null
    /**
     * check whether use publisher, subscriber mode
     * true: create topic connection
     * false: create queue connection
     */
    private var pubSubMode: Boolean = false
    private var startedCount: Int = 0

    private var cacheProducers = true

    private var cacheConsumers = true

    private val connectionRLock: ReentrantReadWriteLock.ReadLock
    private val connectionWLock: ReentrantReadWriteLock.WriteLock

    private val cachedSessions = mutableMapOf<Int, LinkedList<Session>>()

    init {
        val connectionRWLock = ReentrantReadWriteLock()
        connectionRLock = connectionRWLock.readLock()
        connectionWLock = connectionRWLock.writeLock()
    }

    constructor() {

    }

    constructor(connectionFactory: ConnectionFactory) {
        this.targetConnectionFactory = connectionFactory
    }


    override fun createContext(): JMSContext {
        TODO("not implemented")
    }

    override fun createContext(userName: String?, password: String?): JMSContext {
        TODO("not implemented")
    }

    override fun createContext(userName: String?, password: String?, sessionMode: Int): JMSContext {
        TODO("not implemented")
    }

    override fun createContext(sessionMode: Int): JMSContext {
        TODO("not implemented")
    }

    override fun createConnection(): Connection {
        return getSharedConnectionProxy(getConnection())
    }

    override fun createConnection(userName: String?, password: String?): Connection {
        TODO("not implemented")
    }

    protected fun getConnection(): Connection {
        if (this.connection == null) {
            try {
                connectionWLock.lock()
                return if (this.connection != null) {
                    this.connection!!
                } else {
                    initConnection()
                    this.connection!!
                }
            } finally {
                connectionWLock.unlock()
            }
        } else {
            connectionRLock.lock()
            try {
                return if (this.connection != null) {
                    this.connection!!
                } else {
                    return getConnection()
                }
            } finally {
                connectionRLock.unlock()
            }

        }
    }

    @Throws(JmsException::class)
    protected fun initConnection() {
        if (targetConnectionFactory == null) {
            throw IllegalStateException("targetConnectionFactory is null")
        }
        connectionRLock.lock()
        try {
            if (this.connection != null) {
                closeConnection(this.connection!!)
            }
            this.connection = doCreateConnection()
            prepareConnection(this.connection!!)
            if (this.startedCount == 0) {
                this.connection!!.start()
                this.startedCount++
            }
        } finally {
            connectionRLock.unlock()
        }
    }

    protected fun closeConnection(conn: Connection) {
        log.debug("try to close the connection: {}", conn)
        try {
            conn.use {
                if (startedCount > 0) {
                    it.stop()
                }
            }
        } catch (e: JMException) {
            log.error("Throw JMSException during stop or close connection: {}", e)
        } catch (e: Throwable) {
            log.error("Throw throwable during stop or close connection: {}", e)
        }
    }

    @Throws(JmsException::class)
    protected fun doCreateConnection(): Connection {
        val cf = targetConnectionFactory
        return if (java.lang.Boolean.FALSE == this.pubSubMode && cf is QueueConnectionFactory) {
            cf.createQueueConnection()
        } else if (java.lang.Boolean.TRUE == this.pubSubMode && cf is TopicConnectionFactory) {
            cf.createTopicConnection()
        } else {
            obtainConnectionFactory().createConnection()
        }
    }

    @Throws(JmsException::class)
    private fun prepareConnection(conn: Connection) {

    }

    private fun obtainConnectionFactory(): ConnectionFactory {
        val target = this.targetConnectionFactory
        Assert.notNull(target, "targetConnectionFactory is null")
        return target!!
    }

    private fun getSharedConnectionProxy(conn: Connection): Connection {
        val classes = mutableListOf<Class<out Any>>()
        if (conn is QueueConnection) {
            classes.add(QueueConnection::class.java)
        }
        if (conn is TopicConnection) {
            classes.add(TopicConnection::class.java)
        }
        return Proxy.newProxyInstance(Connection::class.java.classLoader, classes.toTypedArray(), SharedConnectionInvocationHandler()) as Connection
    }

    private fun getSession(conn: Connection, mode: Int): Session? {
        val sessionList = this.cachedSessions.computeIfAbsent(mode) { LinkedList() }
        var session: Session? = null
        synchronized(sessionList) {
            if (sessionList.isNotEmpty()) {
                session = sessionList.remove()
            }
        }
        if (session != null) {
            log.trace("Found cached JMS session for mode {}: {}", mode, session)
        } else {
            val targetSession = doCreateSession(conn, mode)
            session = getCachedSessionProxy(targetSession, sessionList)
        }

        return session
    }

    @Throws(JMSException::class)
    private fun doCreateSession(conn: Connection, mode: Int): Session {
        val transacted = mode == Session.SESSION_TRANSACTED
        val ackMode = if (transacted) Session.AUTO_ACKNOWLEDGE else mode
        // Now actually call the appropriate JMS factory method...
        return if (java.lang.Boolean.FALSE == this.pubSubMode && conn is QueueConnection) {
            (conn as QueueConnection).createQueueSession(transacted, ackMode)
        } else if (java.lang.Boolean.TRUE == this.pubSubMode && conn is TopicConnection) {
            (conn as TopicConnection).createTopicSession(transacted, ackMode)
        } else {
            conn.createSession(transacted, ackMode)
        }
        return conn.createSession(false, mode);
    }

    private fun getCachedSessionProxy(targetSession: Session, sessionList: LinkedList<Session>): Session {
        val classes = mutableListOf<Class<out Any>>()
        if (targetSession is QueueSession) {
            classes.add(QueueSession::class.java)
        }
        if (targetSession is TopicSession) {
            classes.add(TopicSession::class.java)
        }
        return Proxy.newProxyInstance(Session::class.java.classLoader, classes.toTypedArray(), CachedSessionInvocationHandler(targetSession, sessionList)) as Session

    }

    private inner class CachedSessionInvocationHandler(private val target: Session, private val sessionList: LinkedList<Session>) : InvocationHandler {


        private val cachedProducers = mutableMapOf<DestinationCacheKey, MessageProducer>()
        private val cachedConsumers = mutableMapOf<ConsumerCacheKey, MessageConsumer>()

        private var transactionOpen = false

        override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
            val methodName = method!!.getName()
            if (methodName == "equals") {
                // Only consider equal when proxies are identical.
                return proxy === args!![0]
            } else if (methodName == "hashCode") {
                // Use hashCode of Session proxy.
                return System.identityHashCode(proxy)
            } else if (methodName == "toString") {
                return "Cached JMS Session: " + this.target
            } else if (methodName == "close") {
//                // Handle close method: don't pass the call on.
                /**
                 * If cached sessions' size does not reach the limit
                 * just close the Session logically and add the session back to cached session map
                 */
                synchronized(this.sessionList) {
                    if (this.sessionList.size < sessionCacheSize) {
                        try {
                            logicalClose(proxy as Session)
                            // Remain open in the session list.
                            return null
                        } catch (ex: JMSException) {
                            log.trace("Logical close of cached JMS Session failed - discarding it", ex)
                        }

                    }
                }
                physicalClose()
                return null
            } else if (methodName == "getTargetSession") {
                // Handle getTargetSession method: return underlying Session.
                return this.target
            } else if (methodName == "commit" || methodName == "rollback") {
                this.transactionOpen = false
            } else if (methodName.startsWith("create")) {
                this.transactionOpen = true
                if (cacheProducers && (methodName == "createProducer" ||
                                methodName == "createSender" || methodName == "createPublisher")) {
                    // Destination argument being null is ok for a producer
                    val dest = args!![0] as Destination
                    if (!(dest is TemporaryQueue || dest is TemporaryTopic)) {
                        return getCachedProducer(dest)
                    }
                }
                if (methodName == "createQueue") {
                    return this.target.createQueue(args!![0] as String)
                }
//                else if (isCacheConsumers()) {
//                    // let raw JMS invocation throw an exception if Destination (i.e. args[0]) is null
//                    if (methodName == "createConsumer" || methodName == "createReceiver" ||
//                            methodName == "createSubscriber") {
//                        val dest = args[0] as Destination
//                        if (dest != null && !(dest is TemporaryQueue || dest is TemporaryTopic)) {
//                            return getCachedConsumer(dest,
//                                    if (args.size > 1) args[1] as String else null,
//                                    args.size > 2 && args[2] as Boolean, null,
//                                    false)
//                        }
//                    } else if (methodName == "createDurableConsumer" || methodName == "createDurableSubscriber") {
//                        val dest = args[0] as Destination
//                        if (dest != null) {
//                            return getCachedConsumer(dest,
//                                    if (args.size > 2) args[2] as String else null,
//                                    args.size > 3 && args[3] as Boolean,
//                                    args[1] as String,
//                                    true)
//                        }
//                    } else if (methodName == "createSharedConsumer") {
//                        val dest = args[0] as Destination
//                        if (dest != null) {
//                            return getCachedConsumer(dest,
//                                    if (args.size > 2) args[2] as String else null, null,
//                                    args[1] as String,
//                                    false)
//                        }
//                    } else if (methodName == "createSharedDurableConsumer") {
//                        val dest = args[0] as Destination
//                        if (dest != null) {
//                            return getCachedConsumer(dest,
//                                    if (args.size > 2) args[2] as String else null, null,
//                                    args[1] as String,
//                                    true)
//                        }
//                    }
//                }
            }
            try {
                if (args != null) {
                    return method.invoke(this.target, *args)
                } else {
                    return method.invoke(this.target)
                }
            } catch (ex: InvocationTargetException) {
                throw ex.targetException
            }
        }

        @Throws(JMSException::class)
        private fun getCachedProducer(@Nullable dest: Destination?): MessageProducer {
            val cacheKey = if (dest != null) DestinationCacheKey(dest) else null
            var producer: MessageProducer? = this.cachedProducers[cacheKey]
            if (producer != null) {
                log.trace("Found cached JMS MessageProducer for destination [{}]: {}", dest, producer)
            } else {
                producer = this.target.createProducer(dest)
                log.debug("Registering cached JMS MessageProducer for destination [{}]: {}", dest, producer)
                this.cachedProducers[cacheKey!!] = producer
            }
            return LibraCachedMessageProducer(producer!!)
        }


        /**
         * Not actually call session.close(), just rollback the session if the session is transacted and close and remove all consumers of the session
         * Then return the session back to the cached session map
         */
        @Throws(JMSException::class)
        private fun logicalClose(proxy: Session) {
            // Preserve rollback-on-close semantics.
            if (this.transactionOpen && this.target.transacted) {
                this.transactionOpen = false
                this.target.rollback()
            }
            // Physically close durable subscribers at time of Session close call.
            val it = this.cachedConsumers.entries.iterator()
            while (it.hasNext()) {
                val entry = it.next()
                if (entry.key.subscription != null) {
                    try {
                        entry.value.close()
                    } catch (e: Exception) {
                        log.error("fail to close consumer: {}", entry.value)
                    }
                    it.remove()
                }
            }
            // Allow for multiple close calls...
            var returned = false
            synchronized(this.sessionList) {
                if (!this.sessionList.contains(proxy)) {
                    this.sessionList.addLast(proxy)
                    returned = true
                }
            }
            if (returned) {
                log.trace("Returned cached Session: " + this.target)
            }
        }

        @Throws(JMSException::class)
        private fun physicalClose() {
            log.debug("Closing cached Session: " + this.target)
//            // Explicitly close all MessageProducers and MessageConsumers that
//            // this Session happens to cache...
            try {
                for (producer in this.cachedProducers.values) {
                    try {
                        producer.close()
                    } catch (e: Throwable) {
                        log.error("fail to close producer: {}", producer)
                    }
                }
                for (consumer in this.cachedConsumers.values) {
                    try {
                        consumer.close()
                    } catch (e: Throwable) {
                        log.error("fail to close consumer: {}", consumer)
                    }
                }
            } finally {
                this.cachedProducers.clear()
                this.cachedConsumers.clear()
                // Now physically close the Session.
                this.target.close()
            }

        }
    }

    private inner class SharedConnectionInvocationHandler : InvocationHandler {

        @Nullable
        private var localExceptionListener: ExceptionListener? = null

        private var locallyStarted = false

        @Nullable
        @Throws(Throwable::class)
        override fun invoke(proxy: Any, method: Method, @Nullable args: Array<Any>?): Any? {
            if (method.name == "equals" && args != null) {
                val other = args!![0]
                if (proxy === other) {
                    return true
                }
                if (other == null || !Proxy.isProxyClass(other!!.javaClass)) {
                    return false
                }
                val otherHandler = Proxy.getInvocationHandler(other!!)
                return otherHandler is SharedConnectionInvocationHandler && factory() === (otherHandler as SharedConnectionInvocationHandler).factory()
            } else if (method.name == "hashCode") {
                // Use hashCode of containing SingleConnectionFactory.
                return System.identityHashCode(factory())
            } else if (method.name == "toString") {
                return "Shared JMS Connection: " + getConnection()
            } else if (method.name == "setClientID" && args != null) {
                // Handle setClientID method: throw exception if not compatible.
                val currentClientId = getConnection().getClientID()
                return if (currentClientId != null && currentClientId == args!![0]) {
                    null
                } else {
                    throw javax.jms.IllegalStateException(
                            ("setClientID call not supported on proxy for shared Connection. " + "Set the 'clientId' property on the SingleConnectionFactory instead."))
                }
            } else if (method.name == "setExceptionListener" && args != null) {
                // Handle setExceptionListener method: add to the chain.
//                synchronized(connectionMonitor) {
//                    if (aggregatedExceptionListener != null) {
//                        val listener = args!![0] as ExceptionListener
//                        if (listener !== this.localExceptionListener) {
//                            if (this.localExceptionListener != null) {
//                                aggregatedExceptionListener!!.delegates.remove(this.localExceptionListener)
//                            }
//                            if (listener != null) {
//                                aggregatedExceptionListener!!.delegates.add(listener)
//                            }
//                            this.localExceptionListener = listener
//                        }
//                        return null
//                    } else {
//                        throw javax.jms.IllegalStateException(
//                                "setExceptionListener call not supported on proxy for shared Connection. " +
//                                        "Set the 'exceptionListener' property on the SingleConnectionFactory instead. " +
//                                        "Alternatively, activate SingleConnectionFactory's 'reconnectOnException' feature, " +
//                                        "which will allow for registering further ExceptionListeners to the recovery chain.")
//                    }
//                }
            } else if (method.name == "getExceptionListener") {
//                synchronized(connectionMonitor) {
//                    return if (this.localExceptionListener != null) {
//                        this.localExceptionListener
//                    } else {
//                        getExceptionListener()
//                    }
//                }
            } else if (method.name == "start") {
                localStart()
                return null
            } else if (method.name == "stop") {
                localStop()
                return null
            } else if (method.name == "close") {
                localStop()
//                connectionWLock.lock()
//                synchronized(connectionMonitor) {
//                    if (this.localExceptionListener != null) {
//                        if (aggregatedExceptionListener != null) {
//                            aggregatedExceptionListener!!.delegates.remove(this.localExceptionListener)
//                        }
//                        this.localExceptionListener = null
//                    }
//                }
                return null
            } else if ((method.name == "createSession" || method.name == "createQueueSession" ||
                            method.name == "createTopicSession")) {
                // Default: JMS 2.0 createSession() method
                var mode: Int? = Session.AUTO_ACKNOWLEDGE
                if (args != null) {
                    if (args.size == 1) {
                        // JMS 2.0 createSession(int) method
                        mode = args[0] as Int
                    } else if (args.size == 2) {
                        // JMS 1.1 createSession(boolean, int) method
                        val transacted = args[0] as Boolean
                        val ackMode = args[1] as Int
                        mode = (if (transacted) Session.SESSION_TRANSACTED else ackMode)
                    }
                }
                val session = getSession(getConnection(), mode!!)
                if (session != null) {
                    if (!method.returnType.isInstance(session)) {
                        val msg = "JMS Session does not implement specific domain: " + session
                        try {
                            session.close()
                        } catch (ex: Throwable) {
                            log.trace("Failed to close newly obtained JMS Session", ex)
                        }

                        throw javax.jms.IllegalStateException(msg)
                    }
                    return session
                }
            }
            try {
                return if (args != null) {
                    method.invoke(getConnection(), *args)
                } else {
                    method.invoke(getConnection())
                }
            } catch (ex: InvocationTargetException) {
                throw ex.targetException
            }

        }

        @Throws(JMSException::class)
        private fun localStart() {
//            synchronized(connectionMonitor) {
//                if (!this.locallyStarted) {
//                    this.locallyStarted = true
//                    if (startedCount == 0 && connection != null) {
//                        connection!!.start()
//                    }
//                    startedCount++
//                }
//            }
        }

        @Throws(JMSException::class)
        private fun localStop() {
            connectionWLock.lock()
            if (this.locallyStarted) {
                this.locallyStarted = false
                if (startedCount == 1 && connection != null) {
                    connection!!.stop()
                }
                if (startedCount > 0) {
                    startedCount--
                }
            }
            connectionWLock.unlock()
        }

        private fun factory(): LibraCachingConnectionFactory {
            return this@LibraCachingConnectionFactory
        }
    }

    /**
     * Simple wrapper class around a Destination reference.
     * Used as the cache key when caching MessageProducer objects.
     */
    private inner open class DestinationCacheKey(private val destination: Destination) : Comparable<DestinationCacheKey> {

        @Nullable
        private var destinationString: String? = null

        init {
            Assert.notNull(destination, "Destination must not be null")
        }

        private fun getDestinationString(): String {
            if (this.destinationString == null) {
                this.destinationString = this.destination.toString()
            }
            return this.destinationString!!
        }

        protected fun destinationEquals(otherKey: DestinationCacheKey): Boolean {
            return this.destination.javaClass == otherKey.destination.javaClass && (this.destination == otherKey.destination || getDestinationString() == otherKey.getDestinationString())
        }

        override fun equals(other: Any?): Boolean {
            // Effectively checking object equality as well as toString equality.
            // On WebSphere MQ, Destination objects do not implement equals...
            return this === other || destinationEquals((other as DestinationCacheKey?)!!)
        }

        override fun hashCode(): Int {
            // Can't use a more specific hashCode since we can't rely on
            // this.destination.hashCode() actually being the same value
            // for equivalent destinations... Thanks a lot, WebSphere MQ!
            return this.destination.javaClass.hashCode()
        }

        override fun toString(): String {
            return getDestinationString()
        }

        override fun compareTo(other: DestinationCacheKey): Int {
            return getDestinationString().compareTo(other.getDestinationString())
        }
    }


    /**
     * Simple wrapper class around a Destination and other consumer attributes.
     * Used as the cache key when caching MessageConsumer objects.
     */
    private inner class ConsumerCacheKey(destination: Destination, @param:Nullable @field:Nullable
    private val selector: String?, @param:Nullable @field:Nullable
                                         private val noLocal: Boolean?,
                                         @param:Nullable @field:Nullable
                                         val subscription: String?, private val durable: Boolean) : DestinationCacheKey(destination) {

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            val otherKey = other as ConsumerCacheKey?
            return destinationEquals(otherKey!!) &&
                    ObjectUtils.nullSafeEquals(this.selector, otherKey!!.selector) &&
                    ObjectUtils.nullSafeEquals(this.noLocal, otherKey.noLocal) &&
                    ObjectUtils.nullSafeEquals(this.subscription, otherKey.subscription) &&
                    this.durable == otherKey.durable
        }

        override fun hashCode(): Int {
            return 31 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.selector)
        }

        override fun toString(): String {
            return super.toString() + " [selector=" + this.selector + ", noLocal=" + this.noLocal +
                    ", subscription=" + this.subscription + ", durable=" + this.durable + "]"
        }
    }
}