package com.ivyxjc.libra.core.retry

interface StopStrategy {
    fun shouldStop(attempt: Int): Boolean
}


interface WaitStrategy {
    fun computeSleepTime(attempt: Int): Long
}


class Strategies {
}