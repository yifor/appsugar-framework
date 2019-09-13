package org.appsugar.framework.netty;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocalThread;

public class EventExecutorBindFastThreadLocalThread extends FastThreadLocalThread {

    /**
     * bind to singleThread  event loop
     */
    protected EventExecutor bindEventExecutor;

    /**
     * kotlin coroutine dispatcher preserve
     */
    protected Object kotlinCoroutineDispatcher;

    public EventExecutorBindFastThreadLocalThread() {
    }

    public EventExecutorBindFastThreadLocalThread(Runnable target) {
        super(target);
    }

    public EventExecutorBindFastThreadLocalThread(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public EventExecutorBindFastThreadLocalThread(String name) {
        super(name);
    }

    public EventExecutorBindFastThreadLocalThread(ThreadGroup group, String name) {
        super(group, name);
    }

    public EventExecutorBindFastThreadLocalThread(Runnable target, String name) {
        super(target, name);
    }

    public EventExecutorBindFastThreadLocalThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public EventExecutorBindFastThreadLocalThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    public static EventExecutorBindFastThreadLocalThread currentThread() {
        return (EventExecutorBindFastThreadLocalThread) Thread.currentThread();
    }
}
