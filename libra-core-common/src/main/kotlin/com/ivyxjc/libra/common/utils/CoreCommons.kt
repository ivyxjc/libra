@file:JvmName("CoreCommons")

package com.ivyxjc.libra.common.utils

import com.ivyxjc.libra.common.proxy.LoggerProxy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

//todo change Logger to LoggerProxy
fun loggerFor(clz: Class<*>): Logger = LoggerProxy(LoggerFactory.getLogger(clz))

internal val topLevelClass = object : Any() {}.javaClass.enclosingClass

