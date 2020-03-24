package com.ivyxjc.libra.core.dao

import com.ivyxjc.libra.core.models.RawTransaction
import org.springframework.stereotype.Repository

@Repository
interface RawTransMapper {
    fun insertRaw(raw: RawTransaction)
}

