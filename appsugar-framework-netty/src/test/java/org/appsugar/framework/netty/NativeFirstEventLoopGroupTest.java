package org.appsugar.framework.netty;

import org.junit.jupiter.api.Test;

public class NativeFirstEventLoopGroupTest {

    @Test
    public void testInit() throws Exception {
        NativeFirstEventLoopGroup elg = new NativeFirstEventLoopGroup();
        elg.forEach(e -> {
            e.execute(() -> {
                System.out.println("xxxxxxx " + Thread.currentThread());
            });
        });
        elg.shutdownGracefully().get();
    }
}
