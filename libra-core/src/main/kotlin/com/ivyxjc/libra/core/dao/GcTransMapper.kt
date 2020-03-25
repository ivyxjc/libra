package com.ivyxjc.libra.core.dao

import com.ivyxjc.libra.core.models.GoldCopyTransaction
import org.springframework.stereotype.Repository

@Repository
interface GcTransMapper {

    fun insert(record: GoldCopyTransaction): Int

    fun queryByGuid(gcGuid: Long?): GoldCopyTransaction

    fun updateByGuid(record: GoldCopyTransaction): Int
}
