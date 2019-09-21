package xyz.ivyxjc.libra.core.models

import java.io.Serializable
import java.time.LocalDateTime

abstract class AbstractTransaction :Serializable{

    var createdAt: LocalDateTime? = null
    var createdBy: String? = null
    var createdFrom: String? = null

    var updatedAt: LocalDateTime? = null
    var updatedBy: String? = null
    var updatedFrom: String? = null
}

