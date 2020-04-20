package com.ivyxjc.libra.core.flow

enum class WorkflowSessionMode {
    SESSION,
    GLOBAL
}

interface WorkflowSession {

    fun putString(key: String, value: String?, mode: WorkflowSessionMode)

    fun putStringIfAbsent(key: String, value: String?, mode: WorkflowSessionMode)

    fun put(key: String, value: Any?, mode: WorkflowSessionMode)

    fun putIfAbsent(key: String, value: Any?, mode: WorkflowSessionMode)

    fun putString(key: String, value: String?)

    fun putStringIfAbsent(key: String, value: String?)

    fun put(key: String, value: Any?)

    fun putIfAbsent(key: String, value: Any?)

    companion object {
        fun create(): WorkflowSession {
            return WorkflowSessionDefaultImpl()
        }
    }
}

class WorkflowSessionDefaultImpl internal constructor() : WorkflowSession {

    private val map = mutableMapOf<String, Any?>()

    override fun putString(key: String, value: String?, mode: WorkflowSessionMode) {
        if (mode == WorkflowSessionMode.GLOBAL) {
            TODO("Session mode is not supported")
        }
        map[key] = value
    }

    override fun putStringIfAbsent(key: String, value: String?, mode: WorkflowSessionMode) {
        if (mode == WorkflowSessionMode.GLOBAL) {
            TODO("Session mode is not supported")
        }
        map.putIfAbsent(key, value)
    }

    override fun put(key: String, value: Any?, mode: WorkflowSessionMode) {
        if (mode == WorkflowSessionMode.GLOBAL) {
            TODO("Session mode is not supported")
        }
        map[key] = value
    }

    override fun putIfAbsent(key: String, value: Any?, mode: WorkflowSessionMode) {
        if (mode == WorkflowSessionMode.GLOBAL) {
            TODO("Session mode is not supported")
        }
        map.putIfAbsent(key, value)
    }

    override fun putString(key: String, value: String?) {
        putString(key, value, WorkflowSessionMode.SESSION)
    }

    override fun putStringIfAbsent(key: String, value: String?) {
        putStringIfAbsent(key, value, WorkflowSessionMode.SESSION)
    }

    override fun put(key: String, value: Any?) {
        put(key, value, WorkflowSessionMode.SESSION)
    }

    override fun putIfAbsent(key: String, value: Any?) {
        putIfAbsent(key, value, WorkflowSessionMode.SESSION)
    }

}