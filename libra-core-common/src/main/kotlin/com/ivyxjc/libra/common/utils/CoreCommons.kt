@file:JvmName("CoreCommons")

package com.ivyxjc.libra.common.utils

import com.ivyxjc.libra.common.log.LoggerProxy
import com.ivyxjc.libra.common.log.LoggerProxyImpl
import org.slf4j.LoggerFactory

fun <T> loggerFor(clz: Class<T>): LoggerProxy = LoggerProxyImpl(LoggerFactory.getLogger(clz))

fun loggerFor(clzName: String): LoggerProxy = LoggerProxyImpl(LoggerFactory.getLogger(clzName))

internal val topLevelClass = object : Any() {}.javaClass.enclosingClass

