package com.ivyxjc.libra.core.flow

enum class WorkflowSessionMode {
    SESSION,
    GLOBAL
}

class WorkflowSession {

    private val map = mutableMapOf<String, Any?>()

    fun putString(key: String, value: String?, mode: WorkflowSessionMode) {
        if (mode == WorkflowSessionMode.GLOBAL) {
            TODO("Session mode is not supported")
        }
        map[key] = value
    }

    fun putStringIfAbsent(key: String, value: String?, mode: WorkflowSessionMode) {
        if (mode == WorkflowSessionMode.GLOBAL) {
            TODO("Session mode is not supported")
        }
        map.putIfAbsent(key, value)
    }

    fun put(key: String, value: Any?, mode: WorkflowSessionMode) {
        if (mode == WorkflowSessionMode.GLOBAL) {
            TODO("Session mode is not supported")
        }
        map[key] = value
    }

    fun putIfAbsent(key: String, value: Any?, mode: WorkflowSessionMode) {
        if (mode == WorkflowSessionMode.GLOBAL) {
            TODO("Session mode is not supported")
        }
        map.putIfAbsent(key, value)
    }

    fun putString(key: String, value: String?) {
        putString(key, value, WorkflowSessionMode.SESSION)
    }

    fun putStringIfAbsent(key: String, value: String?) {
        putStringIfAbsent(key, value, WorkflowSessionMode.SESSION)
    }

    fun put(key: String, value: Any?) {
        put(key, value, WorkflowSessionMode.SESSION)
    }

    fun putIfAbsent(key: String, value: Any?) {
        putIfAbsent(key, value, WorkflowSessionMode.SESSION)
    }


}