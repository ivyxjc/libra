package com.ivyxjc.libra.util

import com.google.common.util.concurrent.SimpleTimeLimiter
import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.util.RingBuffer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.random.Random

class RingBufferTest {
    private var bufferSize = 1

    @Suppress("UnstableApiUsage")
    private val limiter = SimpleTimeLimiter.create(Executors.newCachedThreadPool())

    companion object {
        private val log = loggerFor(RingBufferTest::class.java)
    }


    @BeforeEach
    fun init() {
        bufferSize = 2 shl (abs(Random.nextInt()) % 10)
        if (bufferSize < 1) {
            throw RuntimeException("the size should not be less than 1")
        }
        bufferSize = 8
    }

    @Test
    fun testInitErrorArg() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            RingBuffer<Long>(1023)
        }
    }


    @Test
    fun testPut() {
        val ringBuffer = RingBuffer<Long>(1024)
        ringBuffer.put(100)
    }

//    @Test(expected = TimeoutException::class, timeout = 2000)
//    fun testPutBlock() {
//        val ringBuffer = RingBuffer<Long>(bufferSize)
//        limiter.callWithTimeout(Callable {
//            for (i in 0 until bufferSize) {
//                ringBuffer.put(i.toLong())
//            }
//        }, 1, TimeUnit.SECONDS)
//    }

    @Suppress("UnstableApiUsage")
    @Test
    fun testPutNotBlock() {
        val ringBuffer = RingBuffer<Long>(bufferSize)
        limiter.callWithTimeout(Callable {
            for (i in 0 until bufferSize - 1) {
                ringBuffer.put(i.toLong())
            }
        }, Duration.ofSeconds(1))
    }

    @Test
    fun testTake() {
        val ringBuffer = RingBuffer<Long>(1024)
        val t = Random.nextLong()
        ringBuffer.put(t)
        val res = ringBuffer.take()
        Assertions.assertEquals(t, res)
    }

    @Test
    fun testAsyncPutP1C1() {
        val dataSize = abs(Random.nextInt() % 1000) * bufferSize + abs(Random.nextInt() % bufferSize)
        val data = generateAscData(dataSize)
        val ringBuffer = RingBuffer<Long>(bufferSize)
        val executor = Executors.newFixedThreadPool(2)
        val f1 = executor.submit() {
            var prev = Long.MIN_VALUE
            for (i in 0 until dataSize) {
                val res = ringBuffer.take()
                log.debug("res is {}, prev is {}", res, prev)
                Assertions.assertTrue(res > prev)
                if (Long.MIN_VALUE != prev) {
                    Assertions.assertEquals(prev + 1, res)
                }
                prev = res
            }
        }

        val f2 = executor.submit {
            for (i in 0 until dataSize) {
                ringBuffer.put(data[i])
            }
        }
        f1.get()
        f2.get()
    }


    @Test
    fun testAsyncPutPnCn() {
        val dataSize = (abs(Random.nextInt() % 1000) * bufferSize + abs(Random.nextInt() % bufferSize)) / 20
        val data = generateAscData(dataSize * 13)
        val ringBuffer = RingBuffer<Long>(bufferSize)
        val executor = Executors.newFixedThreadPool(16)
        val producers = mutableListOf<Future<*>>()
        val consumers = mutableListOf<Future<*>>()
        val list = CopyOnWriteArrayList<Long>()
        val count = AtomicInteger(-1)
        val randomTimes = abs(Random.nextInt() % 6) + 1
        for (i in 0 until randomTimes) {
            consumers.add(executor.submit() {
                var prev = Long.MIN_VALUE
                for (j in 0 until dataSize) {
                    val res = ringBuffer.take()
                    log.debug("res is {}, prev is {}", res, prev)
                    prev = res
                    list.add(res)
                }
            })
        }

        for (i in 0 until randomTimes) {
            consumers.add(executor.submit() {
                for (j in 0 until dataSize) {
                    val ct = count.addAndGet(1)
                    log.debug("put data: {}", data[ct])
                    ringBuffer.put(data[ct])
                }
            })
        }

        producers.forEach { it.get() }
        consumers.forEach { it.get() }

        val listSet = list.toSet()
        Assertions.assertEquals(list.size, listSet.size)
    }

    @Test
    fun testAsyncPutP1Cn() {
        val dataSize = (abs(Random.nextInt() % 1000) * bufferSize + abs(Random.nextInt() % bufferSize)) / 20
        val data = generateAscData(dataSize * 13)
        val ringBuffer = RingBuffer<Long>(bufferSize)
        val executor = Executors.newFixedThreadPool(16)
        val producers = mutableListOf<Future<*>>()
        val consumers = mutableListOf<Future<*>>()
        val list = CopyOnWriteArrayList<Long>()
        val count = AtomicInteger(-1)
        val randomTimes = abs(Random.nextInt() % 6) + 1
        for (i in 0 until randomTimes) {
            consumers.add(executor.submit() {
                var prev = Long.MIN_VALUE
                for (j in 0 until dataSize) {
                    val res = ringBuffer.take()
                    log.debug("res is {}, prev is {}", res, prev)
                    prev = res
                    list.add(res)
                }
            })
        }

        consumers.add(executor.submit() {
            for (i in 0 until randomTimes) {
                for (j in 0 until dataSize) {
                    val ct = count.addAndGet(1)
                    log.debug("put data: {}", data[ct])
                    ringBuffer.put(data[ct])
                }
            }
        })

        producers.forEach { it.get() }
        consumers.forEach { it.get() }

        val listSet = list.toSet()
        Assertions.assertEquals(list.size, listSet.size)
    }

    @Test
    fun testAAAA() {
        val dataSize = 8
        for (i in 0 until (dataSize + 3)) {
            println(i)
        }
    }

    private fun generateRandomData(size: Int): List<Long> {
        val res = mutableListOf<Long>()
        for (i in 0 until size) {
            res.add(Random.nextLong())
        }
        return res
    }

    private fun generateAscData(size: Int): List<Long> {
        val res = mutableListOf<Long>()
        var base = 0L
        for (i in 0 until size) {
            res.add(base++)
        }
        return res
    }
}