package xyz.ivyxjc.libra.core.models

import org.apache.commons.lang3.builder.ToStringBuilder

class RawTransaction : AbstractTransaction() {
    lateinit var gcGuid: String
    var sourceId: Long = -1
    var rawRecord: String? = null
    var sequence: Long = -1
    var msgId: String? = null

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}