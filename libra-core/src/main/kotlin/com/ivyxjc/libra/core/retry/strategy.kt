package com.ivyxjc.libra.core.retry

import javax.annotation.concurrent.Immutable

interface StopStrategy {
    fun shouldStop(attempt: Int): Boolean
}


interface WaitStrategy {
    fun computeSleepTime(attempt: Int): Long
}


class StopStrategies {

    @Immutable
    class NeverStopStrategy : StopStrategy {
        override fun shouldStop(attempt: Int): Boolean {
            return false
        }
    }

    @Immutable
    class StopAfterAttemptStrategy(private val maxAttempt: Int) : StopStrategy {
        override fun shouldStop(attempt: Int): Boolean {
            return attempt >= maxAttempt
        }
    }

    companion object {

    }
}

class WaitStrategies {

    @Immutable
    class FixedWaitStrategy(val sleepTime: Long) : WaitStrategy {
        override fun computeSleepTime(attempt: Int): Long {
            return sleepTime
        }
    }
}

