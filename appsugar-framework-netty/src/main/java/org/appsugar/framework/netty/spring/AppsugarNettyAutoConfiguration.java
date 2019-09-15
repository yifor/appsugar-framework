package org.appsugar.framework.netty.spring;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import org.appsugar.framework.netty.FastThreadFactory;
import org.appsugar.framework.netty.NativeFirstEventLoopGroup;
import org.appsugar.framework.netty.NettyNativeFirstEventLoopGroupDetector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(EventLoopGroup.class)
@ConditionalOnProperty(prefix = "spring.appsugar.framework.netty", name = "enabled", matchIfMissing = true)
public class AppsugarNettyAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public NettyEventLoopGroupResource nettyResource() {
        int threadCount = Runtime.getRuntime().availableProcessors();
        threadCount = Math.max(1, threadCount);
        FastThreadFactory factory = new FastThreadFactory("netty", true);
        return new NettyEventLoopGroupResource(threadCount, factory);
    }

    @ConditionalOnMissingBean
    @Bean
    public NativeFirstEventLoopGroup globalEventLoopGroup(NettyEventLoopGroupResource resource) {
        return new NativeFirstEventLoopGroup(resource.threadCount, resource.thredFactory);
    }

    @ConditionalOnMissingBean
    @Bean
    public Class<ServerSocketChannel> globalServerSocketChannelClass() {
        return (Class<ServerSocketChannel>) NettyNativeFirstEventLoopGroupDetector.nativeFirstServerSocketChannelClass;
    }

    @ConditionalOnMissingBean
    @Bean
    public Class<SocketChannel> globalSocketChannelClass() {
        return (Class<SocketChannel>) NettyNativeFirstEventLoopGroupDetector.nativeFirstSocketChannelClass;
    }
}
