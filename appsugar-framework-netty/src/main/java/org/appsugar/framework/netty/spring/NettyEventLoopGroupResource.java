package org.appsugar.framework.netty.spring;

import org.appsugar.framework.netty.FastThreadFactory;

public class NettyEventLoopGroupResource {
    public final int threadCount;
    public final FastThreadFactory thredFactory;

    public NettyEventLoopGroupResource(int threadCount, FastThreadFactory thredFactory) {
        this.threadCount = threadCount;
        this.thredFactory = thredFactory;
    }
}
