package org.appsugar.framework.dubbo.spring;

import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration;
import org.appsugar.framework.dubbo.remote.NativeFirstNettyTransporter;
import org.appsugar.framework.netty.NativeFirstEventLoopGroup;
import org.appsugar.framework.netty.spring.AppsugarNettyAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置全局eventloop到nfnt
 */
@ConditionalOnProperty(prefix = "spring.appsugar.framework.dubbo", name = "enabled", matchIfMissing = true)
@ConditionalOnClass(DubboAutoConfiguration.class)
@AutoConfigureBefore(DubboAutoConfiguration.class)
@AutoConfigureAfter(AppsugarNettyAutoConfiguration.class)
@Configuration
public class AppsugarDubboAutoConfiguration {

    @ConditionalOnBean(name = {"globalEventLoopGroup", "globalServerSocketChannelClass", "globalSocketChannelClass"})
    @ConditionalOnClass(DubboAutoConfiguration.class)
    @Bean
    public AppsugarDubboAutoConfiguration configNativeFirst(NativeFirstEventLoopGroup loopGroup, Class<ServerSocketChannel> sclass, Class<SocketChannel> cclass) {
        NativeFirstNettyTransporter.globalEventLoopGroup = loopGroup;
        NativeFirstNettyTransporter.globalServerSocketChannelType = sclass;
        NativeFirstNettyTransporter.globalSocketChannelType = cclass;
        return this;
    }


}
