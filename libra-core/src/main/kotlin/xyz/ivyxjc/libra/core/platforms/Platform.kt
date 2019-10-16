package xyz.ivyxjc.libra.core.platforms

import xyz.ivyxjc.libra.common.utils.loggerFor
import xyz.ivyxjc.libra.core.models.AbstractTransaction
import xyz.ivyxjc.libra.core.models.RawTransaction
import xyz.ivyxjc.libra.core.models.UseCaseTxn

/**
 * All impl should be Thread-safe
 */
interface Dispatcher<out T : AbstractTransaction> {
    fun dispatch(trans: @UnsafeVariance T)
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

class BlankUcTxnDispatcher : Dispatcher<UseCaseTxn> {
    companion object {
        @JvmStatic
        private val log = loggerFor(BlankUcTxnDispatcher::class.java)
    }

    override fun dispatch(trans: UseCaseTxn) {
        log.debug("get ucTxn: {}", trans)
    }

}
