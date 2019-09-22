package xyz.ivyxjc.libra.dao

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import xyz.ivyxjc.libra.buildGcTxn
import xyz.ivyxjc.libra.core.dao.GcTransMapper

@RunWith(SpringRunner::class)
@SpringBootTest
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