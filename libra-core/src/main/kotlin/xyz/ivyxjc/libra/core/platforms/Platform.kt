package xyz.ivyxjc.libra.core.platforms

import xyz.ivyxjc.libra.core.loggerFor
import xyz.ivyxjc.libra.core.models.AbstractTransaction
import xyz.ivyxjc.libra.core.models.RawTransaction
import xyz.ivyxjc.libra.core.models.UsecaseTxn

interface Dispatcher<T : AbstractTransaction> {
    fun dispatch(trans: T)
}

class BlankRawTransDispatcher : Dispatcher<RawTransaction> {
    companion object {
        @JvmStatic
        private val log = loggerFor(BlankRawTransDispatcher::class.java)
    }

    override fun dispatch(trans: RawTransaction) {
        log.debug("get rawTrans: {}", trans)
    }

}

class BlankUcTxnDispatcher : Dispatcher<UsecaseTxn> {
    companion object {
        @JvmStatic
        private val log = loggerFor(BlankUcTxnDispatcher::class.java)
    }

    override fun dispatch(trans: UsecaseTxn) {
        log.debug("get ucTxn: {}", trans)
    }

}
