@file:JvmName("CoreCommons")

package xyz.ivyxjc.libra.core

import xyz.ivyxjc.libra.core.models.RawTransaction
import xyz.ivyxjc.libra.core.models.UsecaseTxn

internal fun rawTransToUcTxn(rawTrans: RawTransaction): UsecaseTxn {
    val res = UsecaseTxn()
    res.sourceId = rawTrans.sourceId
    res.gcGuid = rawTrans.gcGuid
    res.trans = rawTrans
    return res
}