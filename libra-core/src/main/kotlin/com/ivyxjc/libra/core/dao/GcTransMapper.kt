package com.ivyxjc.libra.core.dao

import com.ivyxjc.libra.core.models.GoldCopyTransaction

interface GcTransMapper {

    fun insert(record: GoldCopyTransaction): Int

    fun queryByGuid(gcGuid: Long?): GoldCopyTransaction

    fun updateByGuid(record: GoldCopyTransaction): Int
}
