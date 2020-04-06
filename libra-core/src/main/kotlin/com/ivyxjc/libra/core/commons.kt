@file:JvmName("InternalCoreCommons")

package com.ivyxjc.libra.core

import com.ivyxjc.libra.core.models.RawTransaction
import com.ivyxjc.libra.core.models.UseCaseTxn

internal fun rawTransToUcTxn(rawTrans: RawTransaction): UseCaseTxn {
    val res = UseCaseTxn()
    res.sourceId = rawTrans.sourceId
    res.guid = rawTrans.guid
    res.trans = rawTrans
    return res
}