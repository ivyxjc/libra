package com.ivyxjc.libra.core.dao

import com.ivyxjc.libra.buildGcTxn
import com.ivyxjc.libra.core.TestStartRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMybatis
@ContextConfiguration(classes = [TestStartRunner::class])
open class GcTransMapperTest {

    @Autowired
    private lateinit var gcTransMapper: GcTransMapper

    @Test
    fun testInsert() {
        val gcTxn = buildGcTxn()
        println(gcTxn.gcGuid)
        gcTransMapper.insert(gcTxn)
    }
}