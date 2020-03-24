@file:JvmName("ClassUtils")

package com.ivyxjc.libra.common.utils

import java.lang.reflect.Field

class ClassUtils {
    companion object {

        @JvmStatic
        fun getAllFields(clz: Class<*>): Set<Field> {
            val res = mutableSetOf<Field>()
            var tmpClz = clz
            while (true) {
                res.addAll(tmpClz.declaredFields)
                if (tmpClz.superclass != null) {
                    tmpClz = tmpClz.superclass
                } else {
                    break
                }
            }
            return res
        }

    }
}