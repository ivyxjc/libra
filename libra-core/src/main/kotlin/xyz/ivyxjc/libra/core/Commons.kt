@file:JvmName("CoreCommons")

package xyz.ivyxjc.libra.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.ivyxjc.libra.core.models.RawTransaction
import xyz.ivyxjc.libra.core.models.UsecaseTxn

fun loggerFor(clz: Class<*>): Logger = LoggerFactory.getLogger(clz)

fun rawTransToUcTxn(rawTrans: RawTransaction): UsecaseTxn {
    val res = UsecaseTxn()
    res.sourceId = rawTrans.sourceId
    res.gcGuid = rawTrans.gcGuid
    res.trans = rawTrans
    return res
}