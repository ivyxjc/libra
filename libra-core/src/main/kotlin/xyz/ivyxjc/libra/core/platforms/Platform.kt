package xyz.ivyxjc.libra.core.platforms

import xyz.ivyxjc.libra.core.models.AbstractTransaction

interface Dispatcher<T : AbstractTransaction> {
    fun dispatch(trans: T)
}
