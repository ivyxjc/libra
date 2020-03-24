package xyz.ivyxjc.libra.common.proxy

import org.slf4j.Logger
import org.slf4j.Marker


class LoggerProxy(val log: Logger) : Logger {

    override fun trace(msg: String?) {
        log.trace(msg)
    }

    override fun trace(format: String?, arg: Any?) {
        log.trace(format, arg)
    }

    override fun trace(format: String?, vararg arguments: Any?) {
        log.trace(format, arguments)
    }

    override fun trace(format: String?, arg1: Any?, arg2: Any?) {
        log.trace(format, arg1, arg2)
    }

    override fun trace(msg: String?, t: Throwable?) {
        log.trace(msg, t)
    }

    override fun trace(marker: Marker?, msg: String?) {
        log.trace(marker, msg)
    }

    override fun trace(marker: Marker?, format: String?, arg: Any?) {
        log.trace(marker, format, arg)
    }

    override fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        log.trace(marker, format, arg1, arg2)
    }

    override fun trace(marker: Marker?, format: String?, vararg arguments: Any?) {
        log.trace(marker, format, arguments)
    }

    override fun trace(marker: Marker?, msg: String?, t: Throwable?) {
        log.trace(marker, msg, t)
    }

    override fun debug(msg: String?) {
        log.debug(msg)
    }

    override fun debug(format: String?, arg: Any?) {
        log.debug(format, arg)
    }

    override fun debug(format: String?, vararg arguments: Any?) {
        log.debug(format, arguments)
    }

    override fun debug(format: String?, arg1: Any?, arg2: Any?) {
        log.debug(format, arg1, arg2)
    }

    override fun debug(msg: String?, t: Throwable?) {
        log.debug(msg, t)
    }

    override fun debug(marker: Marker?, msg: String?) {
        log.debug(marker, msg)
    }

    override fun debug(marker: Marker?, format: String?, arg: Any?) {
        log.debug(marker, format, arg)
    }

    override fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        log.debug(marker, format, arg1, arg2)
    }

    override fun debug(marker: Marker?, format: String?, vararg arguments: Any?) {
        log.debug(marker, format, arguments)
    }

    override fun debug(marker: Marker?, msg: String?, t: Throwable?) {
        log.debug(marker, msg, t)
    }

    override fun info(msg: String?) {
        log.info(msg)
    }

    override fun info(format: String?, arg: Any?) {
        log.info(format, arg)
    }

    override fun info(format: String?, vararg arguments: Any?) {
        log.info(format, arguments)
    }

    override fun info(format: String?, arg1: Any?, arg2: Any?) {
        log.info(format, arg1, arg2)
    }

    override fun info(msg: String?, t: Throwable?) {
        log.info(msg, t)
    }

    override fun info(marker: Marker?, msg: String?) {
        log.info(marker, msg)
    }

    override fun info(marker: Marker?, format: String?, arg: Any?) {
        log.info(marker, format, arg)
    }

    override fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        log.info(marker, format, arg1, arg2)
    }

    override fun info(marker: Marker?, format: String?, vararg arguments: Any?) {
        log.info(marker, format, arguments)
    }

    override fun info(marker: Marker?, msg: String?, t: Throwable?) {
        log.info(marker, msg, t)
    }

    override fun warn(msg: String?) {
        log.warn(msg)
    }

    override fun warn(format: String?, arg: Any?) {
        log.warn(format, arg)
    }

    override fun warn(format: String?, vararg arguments: Any?) {
        log.warn(format, arguments)
    }

    override fun warn(format: String?, arg1: Any?, arg2: Any?) {
        log.warn(format, arg1, arg2)
    }

    override fun warn(msg: String?, t: Throwable?) {
        log.warn(msg, t)
    }

    override fun warn(marker: Marker?, msg: String?) {
        log.warn(marker, msg)
    }

    override fun warn(marker: Marker?, format: String?, arg: Any?) {
        log.warn(marker, format, arg)
    }

    override fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        log.warn(marker, format, arg1, arg2)
    }

    override fun warn(marker: Marker?, format: String?, vararg arguments: Any?) {
        log.warn(marker, format, arguments)
    }

    override fun warn(marker: Marker?, msg: String?, t: Throwable?) {
        log.warn(marker, msg, t)
    }

    override fun error(msg: String?) {
        log.error(msg)
    }

    override fun error(format: String?, arg: Any?) {
        log.error(format, arg)
    }

    override fun error(format: String?, vararg arguments: Any?) {
        log.error(format, arguments)
    }

    override fun error(format: String?, arg1: Any?, arg2: Any?) {
        log.error(format, arg1, arg2)
    }

    override fun error(msg: String?, t: Throwable?) {
        log.error(msg, t)
    }

    override fun error(marker: Marker?, msg: String?) {
        log.error(marker, msg)
    }

    override fun error(marker: Marker?, format: String?, arg: Any?) {
        log.error(marker, format, arg)
    }

    override fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        log.error(marker, format, arg1, arg2)
    }

    override fun error(marker: Marker?, format: String?, vararg arguments: Any?) {
        log.error(marker, format, arguments)
    }

    override fun error(marker: Marker?, msg: String?, t: Throwable?) {
        log.error(marker, msg, t)
    }

    override fun isTraceEnabled(): Boolean {
        return log.isTraceEnabled
    }

    override fun isTraceEnabled(marker: Marker?): Boolean {
        return log.isTraceEnabled(marker)
    }

    override fun isDebugEnabled(): Boolean {
        return log.isDebugEnabled
    }

    override fun isDebugEnabled(marker: Marker?): Boolean {
        return log.isDebugEnabled(marker)
    }

    override fun isInfoEnabled(): Boolean {
        return log.isInfoEnabled
    }

    override fun isInfoEnabled(marker: Marker?): Boolean {
        return log.isInfoEnabled(marker)
    }

    override fun isWarnEnabled(): Boolean {
        return log.isWarnEnabled
    }

    override fun isWarnEnabled(marker: Marker?): Boolean {
        return log.isWarnEnabled(marker)
    }

    override fun isErrorEnabled(): Boolean {
        return log.isErrorEnabled
    }

    override fun isErrorEnabled(marker: Marker?): Boolean {
        return log.isErrorEnabled(marker)
    }

    override fun getName(): String {
        return log.name
    }
}