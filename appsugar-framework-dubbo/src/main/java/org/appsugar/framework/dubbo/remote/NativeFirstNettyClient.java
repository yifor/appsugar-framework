package org.appsugar.framework.dubbo.remote;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.ConfigUtils;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.transport.netty4.NettyClient;
import org.apache.dubbo.remoting.transport.netty4.NettyClientHandler;
import org.apache.dubbo.remoting.transport.netty4.NettyCodecAdapter;
import org.apache.dubbo.remoting.utils.UrlUtils;

import java.net.InetSocketAddress;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class NativeFirstNettyClient extends NettyClient {

    public NativeFirstNettyClient(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
    }

    @Override
    protected void doOpen() throws Throwable {
        EventLoopGroup elg = NativeFirstNettyTransporter.globalEventLoopGroup;
        if (elg == null) {
            super.doOpen();
            return;
        }
        final NettyClientHandler nettyClientHandler = new NettyClientHandler(getUrl(), this);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(elg)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getTimeout())
                .channel(NativeFirstNettyTransporter.globalSocketChannelType);

        if (getConnectTimeout() < 3000) {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
        } else {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectTimeout());
        }

        bootstrap.handler(new ChannelInitializer() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                int heartbeatInterval = UrlUtils.getHeartbeat(getUrl());
                NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl(), NativeFirstNettyClient.this);
                ch.pipeline()//.addLast("logging",new LoggingHandler(LogLevel.INFO))//for debug
                        .addLast("decoder", adapter.getDecoder())
                        .addLast("encoder", adapter.getEncoder())
                        .addLast("client-idle-handler", new IdleStateHandler(heartbeatInterval, 0, 0, MILLISECONDS))
                        .addLast("handler", nettyClientHandler);
                String socksProxyHost = ConfigUtils.getProperty("socksProxyHost");
                if (socksProxyHost != null) {
                    int socksProxyPort = Integer.parseInt(ConfigUtils.getProperty("socksProxyPort", "1080"));
                    Socks5ProxyHandler socks5ProxyHandler = new Socks5ProxyHandler(new InetSocketAddress(socksProxyHost, socksProxyPort));
                    ch.pipeline().addFirst(socks5ProxyHandler);
                }
            }
        });
        setSuperBootstrap(bootstrap);
    }

    protected static String BOOTSTRAP_FIELD_NAME = "bootstrap";

    protected void setSuperBootstrap(Bootstrap bootStrap) {
        NativeFirstNettyTransporter.setSuperValueByName(NettyClient.class, this, BOOTSTRAP_FIELD_NAME, bootStrap);
    }

}
