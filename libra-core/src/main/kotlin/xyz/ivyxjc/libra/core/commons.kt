@file:JvmName("InternalCoreCommons")

package xyz.ivyxjc.libra.core

import xyz.ivyxjc.libra.core.models.RawTransaction
import xyz.ivyxjc.libra.core.models.UseCaseTxn

internal fun rawTransToUcTxn(rawTrans: RawTransaction): UseCaseTxn {
    val res = UseCaseTxn()
    res.sourceId = rawTrans.sourceId
    res.gcGuid = rawTrans.gcGuid
    res.trans = rawTrans
    return res
}