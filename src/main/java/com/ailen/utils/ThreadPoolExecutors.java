package com.ailen.utils;


import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutors {
    private static final int processorNumber =
            Runtime.getRuntime().availableProcessors();
    private static final int corePoolSize = processorNumber;
    private static final int maximumPoolSize = processorNumber * 2 + 1;
    private static final int queueSize = 100;

    private static class ThreadPoolExecutorsHolder {
        private static final ThreadPoolExecutor INSTANCE =
                new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                        200,TimeUnit.MILLISECONDS,
                        new LinkedBlockingDeque<>(queueSize));
    }

    private ThreadPoolExecutors() {
    }

    public static ThreadPoolExecutor getSingletonExecutor() {
        System.out.println("处理器数量：" + processorNumber);

        return ThreadPoolExecutorsHolder.INSTANCE;
    }

    public static int getQueueSize() {
        return queueSize;
    }
}
