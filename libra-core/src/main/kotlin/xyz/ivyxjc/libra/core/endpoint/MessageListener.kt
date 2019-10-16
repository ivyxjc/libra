package xyz.ivyxjc.libra.core.endpoint

import org.apache.activemq.artemis.jms.client.ActiveMQBytesMessage
import org.springframework.beans.factory.InitializingBean
import xyz.ivyxjc.libra.common.utils.loggerFor
import xyz.ivyxjc.libra.core.models.AbstractTransaction
import xyz.ivyxjc.libra.core.models.RawTransaction
import xyz.ivyxjc.libra.core.models.UsecaseTxn
import xyz.ivyxjc.libra.core.models.protoModels.ProtoRawTransaction
import xyz.ivyxjc.libra.core.models.protoModels.ProtoUsecaseTxn
import xyz.ivyxjc.libra.core.platforms.Dispatcher
import javax.jms.BytesMessage
import javax.jms.Message
import javax.jms.MessageListener


abstract class AbstractMessageListener() : MessageListener, InitializingBean {

    lateinit var sourceIds: Array<Long>

    lateinit var dispatcher: Dispatcher<AbstractTransaction>

    companion object {
        @JvmStatic
        private val log = loggerFor(AbstractMessageListener::class.java)
    }

    private lateinit var sourceIdsSet: Set<Long>
    protected var sourceId: Long? = null

    override fun afterPropertiesSet() {
        sourceIdsSet = sourceIds.toSet()
        sourceId = if (sourceIds.size == 1) {
            sourceIds[0]
        } else {
            null
        }
    }

    protected fun handleSourceId(msgSourceId: Long?): Long? {
        if (msgSourceId == null) {
            return if (sourceId != null) {
                sourceId
            } else {
                log.error("receive msg with no sourceId and the endpoint does not have exact one sourceId")
                null
            }
        } else {
            return if (sourceIdsSet.isNotEmpty() && !sourceIdsSet.contains(msgSourceId)) {
                log.error("receive msg with unaccepted source")
                return null
            } else {
                msgSourceId
            }
        }
    }

}


class UsecaseTxnMessageListener() : AbstractMessageListener() {

    companion object {
        @JvmStatic
        private val log = loggerFor(UsecaseTxnMessageListener::class.java)
    }

    override fun onMessage(message: Message?) {
        log.debug("receive message: {}", message)
        if (message == null) {
            return
        }
        if (message is ActiveMQBytesMessage) {
            val size = message.bodyLength
            val bytes = ByteArray(size.toInt())
            message.readBytes(bytes)
            val pUcTxn = ProtoUsecaseTxn.PUsecaseTxn.parseFrom(bytes)
            val ucTxn = UsecaseTxn()
            val tmpSourceId = handleSourceId(pUcTxn.sourceId)
            if (tmpSourceId != null) {
                ucTxn.sourceId = tmpSourceId
            } else {
                log.error("cannot handle the source id for message: ${pUcTxn.gcGuid}")
                return
            }
            ucTxn.attributes.putAll(pUcTxn.attributesMap)
            ucTxn.gcGuid = pUcTxn.gcGuid
            dispatcher.dispatch(ucTxn)
        }
    }
}

class RawTransactionMessageListener : AbstractMessageListener() {

    companion object {
        @JvmStatic
        private val log = loggerFor(RawTransactionMessageListener::class.java)
    }

    override fun onMessage(message: Message?) {
        log.debug("receive message: {}", message)
        if (message == null) {
            return
        }
        when (message) {
            is BytesMessage -> {
                val size = message.bodyLength
                val bytes = ByteArray(size.toInt())
                message.readBytes(bytes)
                val pRawTrans = ProtoRawTransaction.PRawTransaction.parseFrom(bytes)
                val rawTrans = RawTransaction()
                val tmpSourceId = handleSourceId(pRawTrans.sourceId)
                if (tmpSourceId != null) {
                    rawTrans.sourceId = tmpSourceId
                } else {
                    return
                }
                rawTrans.rawRecord = pRawTrans.rawRecord
                rawTrans.gcGuid = pRawTrans.gcGuid
                //todo check dupliate and version
                rawTrans.duplicateFlg = 0
                rawTrans.version = 0
                dispatcher.dispatch(rawTrans)
            }
            else -> {
                log.info("receive not byte message :{}", message)
            }
        }
    }

}

class BlankMessageListener : AbstractMessageListener() {
    companion object {
        @JvmStatic
        private val log = loggerFor(BlankMessageListener::class.java)
    }

    override fun onMessage(message: Message?) {
        log.debug("[BlankMessageListener]receive msg: {}", message)
    }
}
