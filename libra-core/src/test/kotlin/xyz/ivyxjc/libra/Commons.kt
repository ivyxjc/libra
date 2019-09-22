package xyz.ivyxjc.libra

import org.apache.commons.lang3.RandomStringUtils
import xyz.ivyxjc.libra.core.models.GoldCopyTransaction
import xyz.ivyxjc.libra.core.models.RawTransaction
import kotlin.random.Random

fun buildRawTrans(): RawTransaction {
    val res = RawTransaction()
    res.gcGuid = Random.nextInt().toString()
    res.sourceId = Random.nextInt().toLong()
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
