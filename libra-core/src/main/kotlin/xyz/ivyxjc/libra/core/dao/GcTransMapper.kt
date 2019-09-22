package xyz.ivyxjc.libra.core.dao

import org.springframework.stereotype.Repository
import xyz.ivyxjc.libra.core.models.GoldCopyTransaction

@Repository
interface GcTransMapper {

    fun insert(record: GoldCopyTransaction): Int

    fun queryByGuid(gcGuid: Long?): GoldCopyTransaction

    fun updateByGuid(record: GoldCopyTransaction): Int
}
