package xyz.ivyxjc.libra.core.endpoint

import org.apache.activemq.artemis.jms.client.ActiveMQBytesMessage
import org.springframework.stereotype.Component
import xyz.ivyxjc.libra.common.loggerFor
import xyz.ivyxjc.libra.core.models.RawTransaction
import xyz.ivyxjc.libra.core.models.UsecaseTxn
import xyz.ivyxjc.libra.core.models.protoModels.ProtoRawTransaction
import xyz.ivyxjc.libra.core.models.protoModels.ProtoUsecaseTxn
import xyz.ivyxjc.libra.core.platforms.Dispatcher
import javax.annotation.Resource
import javax.jms.Message
import javax.jms.MessageListener

class UsecaseTxnMessageListener : MessageListener {
    lateinit var dispatcher: Dispatcher<UsecaseTxn>

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
            ucTxn.sourceId = pUcTxn.sourceId
            ucTxn.attributes.putAll(pUcTxn.attributesMap)
            ucTxn.gcGuid = pUcTxn.gcGuid
            dispatcher.dispatch(ucTxn)
        }
    }
}

@Component("rawTransactionMessageListener")
class RawTransactionMessageListener : MessageListener {

    @Resource(name = "transmissionPlatform")
    lateinit var dispatcher: Dispatcher<RawTransaction>

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
            val pRawTrans = ProtoRawTransaction.PRawTransaction.parseFrom(bytes)
            val rawTrans = RawTransaction()
            rawTrans.sourceId = pRawTrans.sourceId
            rawTrans.rawRecord = pRawTrans.rawRecord
            rawTrans.gcGuid = pRawTrans.gcGuid
            dispatcher.dispatch(rawTrans)
        }
    }
}