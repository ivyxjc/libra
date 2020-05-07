package com.ivyxjc.libra.core.id

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GeneratorTest {

    val generator = IdGeneratorLong(IdLoaderLeafImpl("http://192.168.31.102:30001/api/snowflake/get/1"))

    @Test
    fun testGenerate() {
        generator.getId()
    }

    @Test
    fun testGenerateIds() {
        val list = mutableListOf<Long>()
        for (i in 0 until 100) {
            list.add(generator.getId())
        }
        Assertions.assertEquals(list.size, list.toSet().size)
    }

}