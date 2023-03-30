package io.yzecho.zrpc.protocol.handler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author bc.yzecho
 */
public class RpcRequestProcessor {

    // 业务线程池
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024));

    public static void submitRequest(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public static ThreadPoolExecutor getThreadPool() {
        return threadPoolExecutor;
    }

}
