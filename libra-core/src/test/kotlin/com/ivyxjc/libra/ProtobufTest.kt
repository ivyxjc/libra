package com.ivyxjc.libra

import com.ivyxjc.libra.core.models.RawTransaction
import com.ivyxjc.libra.core.models.UseCaseTxn
import com.ivyxjc.libra.core.models.protoModels.ProtoRawTransaction
import com.ivyxjc.libra.core.models.protoModels.ProtoUsecaseTxn
import kotlin.random.Random


fun buildUcBytes(): ByteArray {
    val ucTxn = UseCaseTxn()
    ucTxn.gcGuid = "abcdefgh"
    ucTxn.sourceId = Random.nextInt()
    ucTxn.ucTxnId = Random.nextInt()
    ucTxn.attributes.putIfAbsent(Random.nextLong().toString(), Random.nextLong().toString())
    ucTxn.attributes.putIfAbsent(Random.nextLong().toString(), Random.nextLong().toString())
    ucTxn.attributes.putIfAbsent(Random.nextLong().toString(), Random.nextLong().toString())
    ucTxn.attributes.putIfAbsent(Random.nextLong().toString(), Random.nextLong().toString())

    val builder = ProtoUsecaseTxn.PUseCaseTxn.newBuilder()
    builder.gcGuid = ucTxn.gcGuid
    builder.sourceId = ucTxn.sourceId
    builder.ucTxnId = ucTxn.ucTxnId
    builder.putAllAttributes(ucTxn.attributes)
    val data = builder.build()
    return data.toByteArray()
}

fun buildRawTransBytes(): ByteArray {
    val rawTrans = RawTransaction()
    rawTrans.guid = Random.nextLong().toString()
    rawTrans.sourceId = Random.nextInt()
    rawTrans.sourceId = 3000
    rawTrans.rawRecord = "阿斯顿阿斯顿发撒打发士大夫士大夫十大"
//    rawTrans.rawRecord = RandomStringUtils.random(100)
    println(rawTrans)
    val builder = ProtoRawTransaction.PRawTransaction.newBuilder()
    builder.gcGuid = rawTrans.guid
    builder.sourceId = rawTrans.sourceId
    builder.rawRecord = rawTrans.rawRecord
    val data = builder.build()
    return data.toByteArray()
}
