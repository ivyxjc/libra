package xyz.ivyxjc.libra

import org.apache.commons.lang3.RandomStringUtils
import xyz.ivyxjc.libra.core.models.RawTransaction
import xyz.ivyxjc.libra.core.models.UsecaseTxn
import xyz.ivyxjc.libra.core.models.protoModels.ProtoRawTransaction
import xyz.ivyxjc.libra.core.models.protoModels.ProtoUsecaseTxn
import kotlin.random.Random


fun buildUcBytes(): ByteArray {
    val ucTxn = UsecaseTxn()
    ucTxn.gcGuid = "abcdefgh"
    ucTxn.sourceId = Random.nextLong()
    ucTxn.ucTxnId = Random.nextLong()
    ucTxn.attributes.putIfAbsent(Random.nextLong().toString(), Random.nextLong().toString())
    ucTxn.attributes.putIfAbsent(Random.nextLong().toString(), Random.nextLong().toString())
    ucTxn.attributes.putIfAbsent(Random.nextLong().toString(), Random.nextLong().toString())
    ucTxn.attributes.putIfAbsent(Random.nextLong().toString(), Random.nextLong().toString())

    val builder = ProtoUsecaseTxn.PUsecaseTxn.newBuilder()
    builder.gcGuid = ucTxn.gcGuid
    builder.sourceId = ucTxn.sourceId
    builder.ucTxnId = ucTxn.ucTxnId
    builder.putAllAttributes(ucTxn.attributes)
    val data = builder.build()
    return data.toByteArray()
}

fun buildRawTransBytes(): ByteArray {
    val rawTrans = RawTransaction()
    rawTrans.gcGuid = Random.nextLong().toString()
    rawTrans.sourceId = Random.nextLong()
    rawTrans.rawRecord = RandomStringUtils.random(100)
    println(rawTrans)
    val builder = ProtoRawTransaction.PRawTransaction.newBuilder()
    builder.gcGuid = rawTrans.gcGuid
    builder.sourceId = rawTrans.sourceId
    builder.rawRecord = rawTrans.rawRecord
    val data = builder.build()
    return data.toByteArray()
}
