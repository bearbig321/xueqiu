package com.xueqiu.etf.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RotationScheduler {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public void scheduleAtMinute(Runnable task) {
        executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
