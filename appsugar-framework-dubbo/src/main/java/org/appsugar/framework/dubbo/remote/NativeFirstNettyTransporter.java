package org.appsugar.framework.dubbo.remote;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.*;

import java.lang.reflect.Field;

public class NativeFirstNettyTransporter implements Transporter {
    public static EventLoopGroup globalEventLoopGroup;
    public static Class<? extends ServerSocketChannel> globalServerSocketChannelType;
    public static Class<? extends SocketChannel> globalSocketChannelType;

    protected static void setSuperValueByName(Class<?> clazz, Object instance, String name, Object value) {
        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Server bind(URL url, ChannelHandler handler) throws RemotingException {
        return new NativeFirstNettyServer(url, handler);
    }

    @Override
    public Client connect(URL url, ChannelHandler handler) throws RemotingException {
        return new NativeFirstNettyClient(url, handler);
    }
}
