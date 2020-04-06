@file:JvmName("InternalCoreCommons")

package com.ivyxjc.libra.core

import com.ivyxjc.libra.core.models.RawTransaction
import com.ivyxjc.libra.core.models.UsecaseTxn

internal fun rawTransToUcTxn(rawTrans: RawTransaction): UsecaseTxn {
    val res = UsecaseTxn()
    res.sourceId = rawTrans.sourceId
    res.guid = rawTrans.guid
    res.trans = rawTrans
    return res
}