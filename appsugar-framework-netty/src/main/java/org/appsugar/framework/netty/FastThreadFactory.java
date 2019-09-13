package org.appsugar.framework.netty;

import io.netty.util.concurrent.DefaultThreadFactory;

public class FastThreadFactory extends DefaultThreadFactory {
    public FastThreadFactory(Class<?> poolType) {
        super(poolType);
    }

    public FastThreadFactory(String poolName) {
        super(poolName);
    }

    public FastThreadFactory(Class<?> poolType, boolean daemon) {
        super(poolType, daemon);
    }

    public FastThreadFactory(String poolName, boolean daemon) {
        super(poolName, daemon);
    }

    public FastThreadFactory(Class<?> poolType, int priority) {
        super(poolType, priority);
    }

    public FastThreadFactory(String poolName, int priority) {
        super(poolName, priority);
    }

    public FastThreadFactory(Class<?> poolType, boolean daemon, int priority) {
        super(poolType, daemon, priority);
    }

    public FastThreadFactory(String poolName, boolean daemon, int priority, ThreadGroup threadGroup) {
        super(poolName, daemon, priority, threadGroup);
    }

    public FastThreadFactory(String poolName, boolean daemon, int priority) {
        super(poolName, daemon, priority);
    }

    @Override
    protected Thread newThread(Runnable r, String name) {
        return new EventExecutorBindFastThreadLocalThread(threadGroup, r, name);
    }
}
