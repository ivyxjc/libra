package com.ivyxjc.libra.core.exception

import com.ivyxjc.libra.core.retry.StopStrategy
import com.ivyxjc.libra.core.retry.WaitStrategy

open class BizException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
            message,
            cause,
            enableSuppression,
            writableStackTrace
    )
}

open class InstantRetryException @JvmOverloads constructor(val count: Int, message: String? = null) :
        Exception(message)


open class DelayRetryInMemoryException @JvmOverloads
constructor(val stopStrategy: StopStrategy, val waitStrategy: WaitStrategy, message: String? = null) :
        Exception(message)
