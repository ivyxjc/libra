package xyz.ivyxjc.libra.dao

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import xyz.ivyxjc.libra.buildRawTrans
import xyz.ivyxjc.libra.core.dao.RawTransMapper
import xyz.ivyxjc.libra.core.models.RawTransaction

@RunWith(SpringRunner::class)
@SpringBootTest
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