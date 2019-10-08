package xyz.ivyxjc.libra.core.dao

import org.junit.Test
import org.junit.runner.RunWith
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import xyz.ivyxjc.libra.buildRawTrans
import xyz.ivyxjc.libra.core.TestStartRunner

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMybatis
@ContextConfiguration(classes = [TestStartRunner::class])
open class RawTransMapperTest {

    @Autowired
    private lateinit var mRawTransMapper: RawTransMapper

    @Test
    fun testInsert() {
        val raw = buildRawTrans()
        println(raw.gcGuid)
        mRawTransMapper.insertRaw(raw)
    }


}