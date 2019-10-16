@file:JvmName("ResourceUtils")

package xyz.ivyxjc.libra.common.utils

import java.util.*

private val topLevelClass = object : Any() {}.javaClass.enclosingClass

fun getProperty(key: String, filename: String): String {
    val input = topLevelClass.classLoader.getResourceAsStream(filename)
    val prop = Properties()
    prop.load(input)
    return prop.getProperty(key)
}

fun getProperty(key: String): String {
    return getProperty(key, "private.properties")
}
