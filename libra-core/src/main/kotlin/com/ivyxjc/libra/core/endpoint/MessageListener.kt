package com.ivyxjc.libra.core.endpoint

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.models.AbstractTransaction
import com.ivyxjc.libra.core.models.RawTransaction
import com.ivyxjc.libra.core.models.UseCaseTxn
import com.ivyxjc.libra.core.models.protoModels.ProtoRawTransaction
import com.ivyxjc.libra.core.models.protoModels.ProtoUsecaseTxn
import com.ivyxjc.libra.core.platforms.Dispatcher
import org.apache.activemq.artemis.jms.client.ActiveMQBytesMessage
import org.apache.commons.lang3.StringUtils
import javax.jms.BytesMessage
import javax.jms.Message
import javax.jms.MessageListener


abstract class AbstractMessageListener : MessageListener {
    companion object {
        @JvmStatic
        private val log = loggerFor(AbstractMessageListener::class.java)
    }

    var dispatcher: Dispatcher<AbstractTransaction>? = null

    var sourceIdStr: String = "ALL"
        set(value) {
            val sourceIds: Array<Int>
            if (!StringUtils.equalsIgnoreCase(value, "ALL")) {
                val sourceIdStrList = (value as String).split(",")
                sourceIds = Array(sourceIdStrList.size) {
                    sourceIdStrList[it].toInt()
                }
                this.sourceIdsSet = sourceIds.toSet()
                if (this.sourceIdsSet.size == 1) {
                    this.sourceId = this.sourceIdsSet.first()
                }
            } else {
                this.sourceIdsSet = HashSet()
                this.sourceId = -1
            }
        }

    private lateinit var sourceIdsSet: Set<Int>

    protected var sourceId: Int? = null

    protected fun handleSourceId(msgSourceId: Int?): Int? {
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


class UsecaseTxnMessageListener : AbstractMessageListener() {

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
            val pUcTxn = ProtoUsecaseTxn.PUseCaseTxn.parseFrom(bytes)
            val ucTxn = UseCaseTxn()
            val tmpSourceId = handleSourceId(pUcTxn.sourceId)
            if (tmpSourceId != null) {
                ucTxn.sourceId = tmpSourceId
            } else {
                log.error("cannot handle the source id for message: {}", pUcTxn.gcGuid)
                return
            }
            ucTxn.attributes.putAll(pUcTxn.attributesMap)
            ucTxn.gcGuid = pUcTxn.gcGuid
            dispatcher!!.dispatch(ucTxn)
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
                //todo check duplicate and version
                rawTrans.duplicateFlg = 0
                rawTrans.version = 0
                dispatcher!!.dispatch(rawTrans)
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
