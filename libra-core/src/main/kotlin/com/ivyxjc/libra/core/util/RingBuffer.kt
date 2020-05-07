package com.ivyxjc.libra.core.util

import com.ivyxjc.libra.common.utils.loggerFor
import org.apache.commons.lang3.StringUtils
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.LockSupport

/**
 * this ring buffer is not a full function RingBuffer,
 *
 *
 * The known issues:
 * 1. If there is only one element left in the RingBuffer, you
 * will block on method [RingBuffer.take] (Fixed)
 *
 * -----------------Deprecated Document starts
 * **[RingBuffer.tail] means the position that is filled up by the element, here
 *      ne tricky problem is. If there is only one valid element in the RingBuffer,
 *      can you get it. The answer is no, that is why the know issue[1] happens. The below is
 *      the explanation.
 *      If you make tail as the last valid element's index in RingBuffer, it will result in
 *      one thread-unsafe problem.
 *      If you update the tail first, and you want to take the element from the consumer,
 *      you may consume the position that is not filled up by the producer. Because the thread
 *      may block between atomic add of tail and set element to entries[tail]
 *      If you update the entries first, and the multiple producer may set value in the same position
 *      for more than one time, the reason is as same as the previous one.
 *      So I make the tail refer to the element which is one before the last valid element's index.
 *      This results in the known issue[1], you cannot consume the RingBuffer when there is only one
 *      element left in the RingBuffer
 *      There are two way to fix the issue:
 *          1. make sure that there is only one thread to put the element, and you can make tail the
 *             index of last valid element in entries.
 *          2. make the tail refer to the element which is one before the last valid element's index.
 *              you can put elements in multi-threads and bear one small bug. I used it as the
 *              id container, I can bear the one element's loss.
 * -----------------Deprecated Document ends
 *
 *
 * [thread-safe]
 * @author ivyxjc
 * @since 1.0
 */
class RingBuffer<T>(val bufferSize: Int) {
    companion object {
        private val log = loggerFor(RingBuffer::class.java)
    }

    constructor(bufferSize: Int, loadFactor: Double) : this(bufferSize) {
        this.loadFactor = loadFactor
    }

    var loadFactor = 0.3

    private val entries: Array<ValueContainer<T>>

    /**
     * false: writeable, not readable
     * true: readable, not writeable
     */
    private val flags: Array<Boolean>

    private val tail = AtomicLong(-1)

    private val cursor = AtomicLong(-1)

    private val indexMask: Int

    init {
        require(bufferSize >= 1) { "bufferSize must not be less than 1" }
        require(Integer.bitCount(bufferSize) == 1) { "bufferSize must be a power of 2" }
        @Suppress("RemoveExplicitTypeArguments")
        entries = Array<ValueContainer<T>>(bufferSize) {
            ValueContainer()
        }
        flags = Array(bufferSize) {
            false
        }
        indexMask = bufferSize - 1
    }

    fun put(t: T) {
        val nextTail = tail.updateAndGet {
            val wrapPoint = it + 1 - bufferSize
            var cursorIdx = cursor.get()
            while (wrapPoint >= cursorIdx || flags[calIndex(it + 1)]) {
                log.debug("[loop put] tail is {}, cursor is {}", it, cursorIdx)
                log.debug("put entries is: {}", entries)
                log.debug("put flags is: {}", flags)
                LockSupport.parkNanos(1)
                cursorIdx = cursor.get()
            }
            return@updateAndGet it + 1
        }
        log.debug("[put] tail is {}, data is {}", nextTail, t)
        entries[calIndex(nextTail)].put(t)
        flags[calIndex(nextTail)] = true
        log.debug("put entries is: {}", entries)
    }

    fun take(): T {
        var tailIdx: Long
        val nextCursor = cursor.updateAndGet {
            do {
                tailIdx = tail.get()
                log.trace("[loop take] tail is {}, cursor is {}", tailIdx, it)
                if (it < tailIdx && flags[calIndex(it + 1)]) {
                    break
                }
                LockSupport.parkNanos(1)
            } while (it >= tailIdx || !flags[calIndex(it + 1)])
            return@updateAndGet it + 1
        }
        log.debug("[take] entries is: {}", entries)
        log.debug("[take] flags is {}", flags)
        log.debug("[take] cursor is {}", nextCursor)
        val res = entries[calIndex(nextCursor)].take()
        flags[calIndex(nextCursor)] = false
        return res
    }

    fun needLoad(): Boolean {
        val tail = tail.get()
        val cursor = cursor.get()
        if (tail - cursor < bufferSize * loadFactor) {
            return true
        }
        return false
    }

    fun estimateLoadFactory(): Int {
        var count = 0
        for (i in 0 until bufferSize) {
            if (!flags[i]) {
                count++
            }
        }
        return ((count.toDouble() / bufferSize.toDouble()) * 100).toInt()
    }

    private fun calIndex(seq: Long): Int {
        return (seq and indexMask.toLong()).toInt()
    }

}

class ValueContainer<T> {
    private var t: T? = null
    fun put(t: T) {
        this.t = t
    }

    fun take(): T {
        return this.t!!
    }

    override fun toString(): String {
        return if (t == null) {
            StringUtils.EMPTY
        } else {
            t.toString()
        }
    }
}

fun main() {
    val count = 20
    val bufferSize = 60
    println(((count.toDouble() / bufferSize.toDouble()) * 100).toInt())
}