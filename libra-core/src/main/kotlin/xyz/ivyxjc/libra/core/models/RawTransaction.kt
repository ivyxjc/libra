package xyz.ivyxjc.libra.core.models

class RawTransaction : AbstractTransaction() {
    lateinit var gcGuid: String
    var sourceId: Long = -1
    var rawRecord: String? = null
    var sequence: Long = -1
}