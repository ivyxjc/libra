package com.ivyxjc.libra.core.dao

import com.ivyxjc.libra.buildRawTrans
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
open class RawTransMapperTest {

    @Autowired
    private lateinit var rawTransMapper: RawTransMapper

    @Test
    fun testInsert() {
        val raw = buildRawTrans()
        rawTransMapper.insertRaw(raw)
    }
}