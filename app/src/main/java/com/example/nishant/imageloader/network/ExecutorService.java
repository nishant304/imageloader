package com.example.nishant.imageloader.network;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by nishant on 11/30/2016.
 */

public final class ExecutorService {

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private final ThreadPoolExecutor mDecodeThreadPool;

    public ExecutorService(LinkedBlockingDeque<Runnable> blockingDeque) {
        mDecodeThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                10,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                blockingDeque);
    }

    public  void submit(Runnable runnable) {
        mDecodeThreadPool.submit(runnable);
    }

    public void submit(Callable runnable) {
        mDecodeThreadPool.submit(runnable);
    }
}
