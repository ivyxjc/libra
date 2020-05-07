package com.ivyxjc.libra.core.id

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.util.RingBuffer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.lang3.StringUtils
import java.util.concurrent.Executors


interface IdGenerator<T> {
    fun getId(): T
    fun getIds(size: Int): List<T>
    fun loadKeys(size: Int)
}

interface IdLoader<T> {
    fun loadKeys(ringBuffer: RingBuffer<T>, size: Int)
}

class IdLoaderLeafImpl(url: String) : IdLoader<Long> {
    companion object {
        private val log = loggerFor(IdLoaderLeafImpl::class.java)
    }

    private val client = OkHttpClient.Builder().build()
    private val req = Request.Builder()
        .url(url)
        .get()
        .build()

    override fun loadKeys(ringBuffer: RingBuffer<Long>, size: Int) {
        for (i in 0 until size) {
            val response = client.newCall(req).execute()
            if (response.isSuccessful && response.body != null) {
                val responseStr = response.body!!.string()
                if (StringUtils.isNumeric(responseStr)) {
                    ringBuffer.put(responseStr.toLong())
                } else {
                    log.error("[loadKeys] response is not numeric")
                }
            } else {
                log.error("[loadKeys] response is not successful")
            }
        }
    }
}


class IdGeneratorLong(private val idLoader: IdLoader<Long>) : IdGenerator<Long> {

    companion object {
        private val log = loggerFor(IdGeneratorLong::class.java)
    }

    private val executors = Executors.newFixedThreadPool(4)

    private val ringBuffer = RingBuffer<Long>(1024)

    override fun getId(): Long {
        if (ringBuffer.needLoad()) {
            loadKeys((ringBuffer.bufferSize * (1 - ringBuffer.loadFactor)).toInt())
        }
        return ringBuffer.take()
    }

    override fun getIds(size: Int): List<Long> {
        if (ringBuffer.needLoad()) {
            loadKeys(size)
        }
        val res = mutableListOf<Long>()
        for (i in 0 until size) {
            res.add(ringBuffer.take())
        }
        return res
    }

    override fun loadKeys(size: Int) {
        executors.submit {
            idLoader.loadKeys(ringBuffer, size)
        }
    }
}


