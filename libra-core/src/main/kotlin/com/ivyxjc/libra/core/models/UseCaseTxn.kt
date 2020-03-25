package com.ivyxjc.libra.core.models

class UseCaseTxn : AbstractTransaction() {
    var sourceId: Int = -1
    var usecaseId: Int = -1
    var usecaseName: String = ""
    var usecaseStatus: String = ""
    lateinit var gcGuid: String
    var ucTxnId: Int = -1
    val attributes = mutableMapOf<String, String>()
    var trans: AbstractTransaction? = null

    fun getTransaction(): AbstractTransaction? {
        return trans
    }

    fun getRawTransaction(): RawTransaction? {
        val txn = trans
        return txn as? RawTransaction
    }
}
