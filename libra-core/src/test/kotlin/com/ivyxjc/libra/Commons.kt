package com.ivyxjc.libra

import com.ivyxjc.libra.core.models.GoldCopyTransaction
import com.ivyxjc.libra.core.models.RawTransaction
import org.apache.commons.lang3.RandomStringUtils
import kotlin.random.Random

fun buildRawTrans(): RawTransaction {
    val res = RawTransaction()
    res.gcGuid = Random.nextInt().toString()
    res.sourceId = Random.nextInt()
    res.rawRecord = RandomStringUtils.random(1000)
    return res
}

fun buildGcTxn(): GoldCopyTransaction {
    val res = GoldCopyTransaction()
    res.gcGuid = Random.nextInt().toString()
    res.sourceId = Random.nextInt().toLong()
    res.ssbAssetId = RandomStringUtils.random(10)
    return res
}
