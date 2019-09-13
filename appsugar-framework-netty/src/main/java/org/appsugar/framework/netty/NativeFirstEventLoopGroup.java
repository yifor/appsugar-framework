package org.appsugar.framework.netty;

import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 优先使用本地循环事件组
 * 修改next策略,从原来的AtomicInteger 改成 ThreadLocal<Counter>方式.减少线程资源争抢
 */
public class NativeFirstEventLoopGroup implements EventLoopGroup {
    public static final Consumer<EventExecutor> emptyConsumer = e -> {
    };
    public static final ThreadLocal<Counter> threadCounter = new ThreadLocal<Counter>() {
        @Override
        protected Counter initialValue() {
            return new Counter();
        }
    };
    protected EventLoopGroup delegate;
    protected EventLoop[] unModifiableEventExecutors;
    protected int eventExecutorSize;
    protected Set<EventExecutor> bindEventLoop = ConcurrentHashMap.newKeySet();

    protected Consumer<EventExecutor> eventLoopConsumer;

    public NativeFirstEventLoopGroup() {
        this(0);
    }

    public NativeFirstEventLoopGroup(int nThreads) {
        this(nThreads, new FastThreadFactory(NativeFirstEventLoopGroup.class, true));
    }

    public NativeFirstEventLoopGroup(int nThreads, FastThreadFactory threadFactory) {
        EventLoopGroup elg = NettyNativeFirstEventLoopGroupDetector.newNativeFirstEventLoopGroup(nThreads, threadFactory);
        List<EventLoop> eventExecutorList = new ArrayList<>(nThreads == 0 ? Runtime.getRuntime().availableProcessors() : nThreads);
        elg.forEach(e -> eventExecutorList.add((EventLoop) e));
        this.unModifiableEventExecutors = eventExecutorList.stream().toArray(EventLoop[]::new);
        this.eventExecutorSize = eventExecutorList.size();
        delegate = elg;
        initEventLoopConsumer();
    }

    protected void initEventLoopConsumer() {
        Set<EventExecutor> bindSet = this.bindEventLoop;
        this.eventLoopConsumer = e -> {
            if (bindSet.size() == eventExecutorSize) {
                this.eventLoopConsumer = emptyConsumer;
                return;
            }
            if (bindSet.contains(e)) {
                return;
            }
            bindSet.add(e);
            bindEventExecutorToThread(e);
        };
    }

    protected void bindEventExecutorToThread(EventExecutor eventExecutor) {
        eventExecutor.execute(() -> EventExecutorBindFastThreadLocalThread.currentThread().bindEventExecutor = eventExecutor);
    }

    @Override
    public EventLoop next() {
        Counter counter = threadCounter.get();
        int position = counter.getAndInc() % eventExecutorSize;
        EventLoop el = unModifiableEventExecutors[position];
        eventLoopConsumer.accept(el);
        return el;
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        Iterator<EventExecutor> it = delegate.iterator();
        return new Iterator<EventExecutor>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public EventExecutor next() {
                EventExecutor ee = it.next();
                eventLoopConsumer.accept(ee);
                return ee;
            }
        };
    }


    @Override
    public boolean isShuttingDown() {
        return delegate.isShuttingDown();
    }

    @Override
    public Future<?> shutdownGracefully() {
        return delegate.shutdownGracefully();
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return delegate.shutdownGracefully(quietPeriod, timeout, unit);
    }

    @Override
    public Future<?> terminationFuture() {
        return delegate.terminationFuture();
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }


    @Override
    public Future<?> submit(Runnable task) {
        return next().submit(task);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return next().invokeAll(tasks);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return next().invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return next().invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return next().invokeAny(tasks, timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return next().submit(task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return next().submit(task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return next().schedule(command, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return next().schedule(callable, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return next().scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return next().scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return next().register(channel);
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        return next().register(promise);
    }

    @Override
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        return next().register(channel, promise);
    }

    @Override
    public void execute(Runnable command) {
        next().execute(command);
    }

    static class Counter {
        int count = 0;

        int getAndInc() {
            int result = count++;
            if (result < 0) {
                count = 0;
            }
            return result;
        }
    }
}
