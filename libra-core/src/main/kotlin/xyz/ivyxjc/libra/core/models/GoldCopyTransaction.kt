package xyz.ivyxjc.libra.core.models

import java.math.BigDecimal
import java.time.LocalDateTime

class GoldCopyTransaction {

    lateinit var gcGuid: String

    var sourceId: Long = -1

    var sequence: Int = -1

    var compositeUniqueId: String? = null

    var ssbAssetId: String? = null

    var ssbTradeId: String? = null

    var fundId: String? = null

    var transType: String? = null

    var sourceSystemId: String? = null

    var crCcyBase: String? = null

    var crAcctId: String? = null

    var crAcctOffice: String? = null

    var crTransAmount: BigDecimal? = null

    var crCheck: String? = null

    var crTransType: String? = null

    var crTransRsnCd: String? = null

    var crValueDate: LocalDateTime? = null

    var crRsnCd: String? = null

    var drCcyBase: String? = null

    var drAcctId: String? = null

    var drAcctOffice: String? = null

    var drTransAmount: BigDecimal? = null

    var drCheck: String? = null

    var drTransType: String? = null

    var drTransRsnCd: String? = null

    var drValueDate: LocalDateTime? = null

    var drRsnCd: String? = null

    var referenceId: String? = null

    var relatedReference: String? = null

    var transDesc: String? = null

    var createdAt: LocalDateTime? = null

    var createdBy: String? = null

    var createdFrom: String? = null

    var updatedAt: LocalDateTime? = null

    var updatedBy: String? = null

    var updatedFrom: String? = null
}