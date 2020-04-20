package com.ivyxjc.libra.core.retry.exception

import com.ivyxjc.libra.core.retry.*

abstract class RetryException(message: String? = null, val retryType: RetryType, val stopStrategy: StopStrategy) :
        Exception(message)

abstract class BlockedRetryException(
        message: String?,
        retryType: RetryType,
        stopStrategy: StopStrategy,
        val waitStrategy: WaitStrategy
) :
        RetryException(message, retryType, stopStrategy)

abstract class AsyncRetryException(
        message: String?,
        retryType: RetryType,
        stopStrategy: StopStrategy,
        val waitStrategy: WaitStrategy
) :
        RetryException(message, retryType, stopStrategy)

abstract class AsyncInMemoryRetryException(
        message: String?,
        retryType: RetryType,
        stopStrategy: StopStrategy,
        val waitStrategy: WaitStrategy
) : RetryException(message, retryType, stopStrategy)


/**
 * Processor can only have BlockedRetry Exception
 */

class ProcessorBlockedRetryException
@JvmOverloads constructor(
        message: String? = null,
        stopStrategy: StopStrategy = StopStrategies.StopAfterAttemptStrategy(6),
        waitStrategy: WaitStrategy = WaitStrategies.FixedWaitStrategy(1)
) : BlockedRetryException(message, RetryType.PROCESSOR, stopStrategy, waitStrategy)

class PlatformBlockedRetryException
@JvmOverloads constructor(
        message: String? = null,
        stopStrategy: StopStrategy = StopStrategies.StopAfterAttemptStrategy(6),
        waitStrategy: WaitStrategy = WaitStrategies.FixedWaitStrategy(1)
) : BlockedRetryException(message, RetryType.PLATFORM, stopStrategy, waitStrategy)

class PlatformAsyncRetryException
@JvmOverloads constructor(
        message: String? = null,
        stopStrategy: StopStrategy = StopStrategies.StopAfterAttemptStrategy(6),
        waitStrategy: WaitStrategy = WaitStrategies.FixedWaitStrategy(1)
) : AsyncRetryException(message, RetryType.PLATFORM, stopStrategy, waitStrategy)

class PlatformAsyncInMemoryRetryException
@JvmOverloads constructor(
        message: String? = null,
        stopStrategy: StopStrategy = StopStrategies.StopAfterAttemptStrategy(6),
        waitStrategy: WaitStrategy = WaitStrategies.FixedWaitStrategy(1)
) : AsyncInMemoryRetryException(message, RetryType.PLATFORM, stopStrategy, waitStrategy)
