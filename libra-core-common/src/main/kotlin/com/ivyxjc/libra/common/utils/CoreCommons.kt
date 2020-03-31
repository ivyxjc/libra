@file:JvmName("CoreCommons")

package com.ivyxjc.libra.common.utils

import com.ivyxjc.libra.common.proxy.LoggerProxy
import com.ivyxjc.libra.common.proxy.LoggerProxyImpl
import org.slf4j.LoggerFactory

fun <T> loggerFor(clz: Class<T>): LoggerProxy = LoggerProxyImpl(LoggerFactory.getLogger(clz))

fun <T : Any> T.loggerFor(): Lazy<LoggerProxy> {
    return lazy { LoggerProxyImpl(LoggerFactory.getLogger(this::class.java)) }
}

internal val topLevelClass = object : Any() {}.javaClass.enclosingClass

