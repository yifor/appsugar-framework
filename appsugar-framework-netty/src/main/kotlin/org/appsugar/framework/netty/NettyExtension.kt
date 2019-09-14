package org.appsugar.framework.netty

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher

/**
 * 获取当前线程绑定的dispatcher
 * 如果当前线程不是EventExecutorBindFastThreadLocalThread 线程, 那么返回null
 */
fun getCurrentDispatcher(): CoroutineDispatcher? {
    val thread = EventExecutorBindFastThreadLocalThread.currentThread() ?: return null
    val dispatcher = thread.kotlinCoroutineDispatcher
    if (dispatcher == null) {
        val d = thread.bindEventExecutor!!.asCoroutineDispatcher()
        thread.kotlinCoroutineDispatcher = d
        return d
    }
    return dispatcher as CoroutineDispatcher
}
