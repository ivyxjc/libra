package com.ivyxjc.libra.core.retry.exception

import com.ivyxjc.libra.core.retry.StopStrategy
import com.ivyxjc.libra.core.retry.WaitStrategy

class InstantRetryException @JvmOverloads constructor(val count: Int, message: String? = null) :
        Exception(message)


class DelayRetryInMemoryException @JvmOverloads
constructor(val stopStrategy: StopStrategy, val waitStrategy: WaitStrategy, message: String? = null) :
        Exception(message)