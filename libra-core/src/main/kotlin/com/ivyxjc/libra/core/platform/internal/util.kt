package com.ivyxjc.libra.core.platform.internal

import com.ivyxjc.libra.core.models.RawTransaction
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.models.protoModels.ProtoRawTransaction

internal fun rawTransToUcTxn(rawTrans: RawTransaction): UsecaseTxn {
    val res = UsecaseTxn()
    res.sourceId = rawTrans.sourceId
    res.guid = rawTrans.guid
    res.trans = rawTrans
    return res
}

//internal fun protobufToRawTrans(pPawTxn: ProtoRawTransaction.PRawTransaction): RawTransaction {
//
//}

internal fun protobufFromRawTrans(rawTxn: RawTransaction): ProtoRawTransaction.PRawTransaction {
    return ProtoRawTransaction.PRawTransaction.newBuilder()
            .setGuid(rawTxn.guid)
            .setSourceId(rawTxn.sourceId)
            .setRawRecord(rawTxn.rawRecord)
            .setSequence(rawTxn.sequence)
            .setMsgId(rawTxn.msgId)
            .setVersion(rawTxn.version)
            .setDuplicateFlg(rawTxn.duplicateFlg)
            .build()
}
