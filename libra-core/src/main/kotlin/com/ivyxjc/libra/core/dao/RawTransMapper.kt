package com.ivyxjc.libra.core.dao

import com.ivyxjc.libra.core.models.RawTransaction

interface RawTransMapper {
    fun insertRaw(raw: RawTransaction)
}

