package org.appsugar.framework.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 *
 */
public class NettyNativeFirstEventLoopGroupDetector {
    public static final String EPOOL_CLASS_NAME = "io.netty.channel.epoll.Epoll";
    public static final String KQUEUE_CLASS_NAME = "io.netty.channel.kqueue.KQueue";
    /**
     * is epool support
     */
    public static final boolean isEpoll = isEpollSupport();
    /**
     * is kqueue support
     */
    public static final boolean isKQueue = isKQueueSupport();

    public static final Class<? extends ServerSocketChannel> nativeFirstServerSocketChannelClass = detectNativeFirstServerSocketChannelClass();
    public static final Class<? extends SocketChannel> nativeFirstSocketChannelClass = detectNativeFirstSocketChannelClass();
    public static final Class<? extends EventLoopGroup> nativeFirstEventLoopGroupClass = detectNativeFirstEventLoopGroupClass();


    public static final EventLoopGroup newNativeFirstEventLoopGroup() {
        return newNativeFirstEventLoopGroup(0);
    }

    public static final EventLoopGroup newNativeFirstEventLoopGroup(int threadCount) {
        return newNativeFirstEventLoopGroup(threadCount, new FastThreadFactory("netty", true));
    }

    public static final EventLoopGroup newNativeFirstEventLoopGroup(int threadCount, FastThreadFactory factory) {
        if (isEpoll) {
            return new EpollEventLoopGroup(threadCount, factory);
        } else if (isKQueue) {
            return new KQueueEventLoopGroup(threadCount, factory);
        } else {
            return new NioEventLoopGroup(threadCount, factory);
        }
    }

    private static final Class<? extends EventLoopGroup> detectNativeFirstEventLoopGroupClass() {
        if (isEpoll) {
            return EpollEventLoopGroup.class;
        } else if (isKQueue) {
            return KQueueEventLoopGroup.class;
        } else {
            return NioEventLoopGroup.class;
        }
    }

    private static final Class<? extends ServerSocketChannel> detectNativeFirstServerSocketChannelClass() {
        if (isEpoll) {
            return EpollServerSocketChannel.class;
        } else if (isKQueue) {
            return KQueueServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }

    private static final Class<? extends SocketChannel> detectNativeFirstSocketChannelClass() {
        if (isEpoll) {
            return EpollSocketChannel.class;
        } else if (isKQueue) {
            return KQueueSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }

    private static final boolean isEpollSupport() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            cl.loadClass(EPOOL_CLASS_NAME);
            return Epoll.isAvailable();
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private static final boolean isKQueueSupport() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            cl.loadClass(KQUEUE_CLASS_NAME);
            return KQueue.isAvailable();
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

}
