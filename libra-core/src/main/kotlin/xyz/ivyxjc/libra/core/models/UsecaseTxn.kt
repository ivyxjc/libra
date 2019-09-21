package xyz.ivyxjc.libra.core.models

class UsecaseTxn : AbstractTransaction() {
    var sourceId: Long = -1
    lateinit var gcGuid: String
    var ucTxnId: Long = -1
    val attributes = mutableMapOf<String, String>()
    var trans: AbstractTransaction? = null

    fun getTransaction(): AbstractTransaction? {
        return trans
    }

    fun getRawTransaction(): RawTransaction? {
        val txn = trans
        return txn as RawTransaction
    }
}
