package org.appsugar.framework.dubbo.remote;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.transport.netty4.NettyCodecAdapter;
import org.apache.dubbo.remoting.transport.netty4.NettyServer;
import org.apache.dubbo.remoting.transport.netty4.NettyServerHandler;
import org.apache.dubbo.remoting.utils.UrlUtils;

import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class NativeFirstNettyServer extends NettyServer {
    public NativeFirstNettyServer(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
    }

    @Override
    protected void doOpen() throws Throwable {
        EventLoopGroup elg = NativeFirstNettyTransporter.globalEventLoopGroup;
        if (elg == null) {
            super.doOpen();
            return;
        }
        ServerBootstrap bootstrap = new ServerBootstrap();
        final NettyServerHandler nettyServerHandler = new NettyServerHandler(getUrl(), this);
        setSuperChannels(nettyServerHandler.getChannels());
        bootstrap.group(elg)
                .channel(NativeFirstNettyTransporter.globalServerSocketChannelType)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        int idleTimeout = UrlUtils.getIdleTimeout(getUrl());
                        NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl(), NativeFirstNettyServer.this);
                        ch.pipeline()//.addLast("logging",new LoggingHandler(LogLevel.INFO))//for debug
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, idleTimeout, MILLISECONDS))
                                .addLast("handler", nettyServerHandler);
                    }
                });
        // bind
        ChannelFuture channelFuture = bootstrap.bind(getBindAddress());
        channelFuture.syncUninterruptibly();
        setSuperChannel(channelFuture.channel());
    }

    public static final String CHANNELS_FIELD_NAME = "channels";

    protected void setSuperChannels(Map<String, org.apache.dubbo.remoting.Channel> channels) {
        NativeFirstNettyTransporter.setSuperValueByName(NettyServer.class, this, CHANNELS_FIELD_NAME, channels);
    }


    public static final String CHANNEL_FIELD_NAME = "channel";

    protected void setSuperChannel(Channel channel) {
        NativeFirstNettyTransporter.setSuperValueByName(NettyServer.class, this, CHANNEL_FIELD_NAME, channel);
    }
}
