@file:JvmName("CoreCommons")

package xyz.ivyxjc.libra.common.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.ivyxjc.libra.common.proxy.LoggerProxy

//todo change Logger to LoggerProxy
fun loggerFor(clz: Class<*>): Logger = LoggerProxy(LoggerFactory.getLogger(clz))

internal val topLevelClass = object : Any() {}.javaClass.enclosingClass

