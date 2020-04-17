package com.ivyxjc.libra.core.retry.exception

import com.ivyxjc.libra.core.retry.RetryType
import com.ivyxjc.libra.core.retry.StopStrategy
import com.ivyxjc.libra.core.retry.WaitStrategy


class InstantRetryException
@JvmOverloads constructor(val stopStrategy: StopStrategy,
                          val retryType: RetryType = RetryType.PLATFORM,
                          message: String? = null) :
        Exception(message)


class DelayRetryInMemoryException
@JvmOverloads constructor(val stopStrategy: StopStrategy,
                          val waitStrategy: WaitStrategy,
                          val retryType: RetryType = RetryType.PLATFORM,
                          message: String? = null) :
        Exception(message)