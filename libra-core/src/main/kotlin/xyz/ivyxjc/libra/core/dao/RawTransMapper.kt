package xyz.ivyxjc.libra.core.dao

import org.springframework.stereotype.Repository
import xyz.ivyxjc.libra.core.models.RawTransaction

@Repository
interface RawTransMapper {
    fun insertRaw(raw: RawTransaction)
}

