package xyz.ivyxjc.libra.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun loggerFor(clz: Class<*>): Logger = LoggerFactory.getLogger(clz)