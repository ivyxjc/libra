package com.ivyxjc.libra.core.models

class UsecaseTxn : AbstractTransaction() {
    lateinit var guid: String

    var sourceId: Int = -1
    var usecaseId: Int = -1
    var usecaseName: String = ""
    var usecaseStatus: String = ""
    var ucTxnId: Int = -1
    val attributes = mutableMapOf<String, String>()
    var trans: AbstractTransaction? = null
    var attempt: Int = 0

    fun getTransaction(): AbstractTransaction? {
        return trans
    }

    fun getRawTransaction(): RawTransaction? {
        val txn = trans
        return txn as? RawTransaction
    }
}
