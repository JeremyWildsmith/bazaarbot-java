package com.bazaarbot.runner;

import com.bazaarbot.TimerHelper;
import com.bazaarbot.economy.IEconomy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Nick Gritsenko
 */
public class DurationBasedRunner implements IRunner {
    private static final Logger LOG = LoggerFactory.getLogger(DurationBasedRunner.class);


    private final IEconomy economy;
    private final Duration duration;
    private final ExecutorService executorService;

    public DurationBasedRunner(IEconomy economy, Duration simulationDuration) {
        this.economy = economy;
        this.duration = simulationDuration;
        this.executorService = Executors.newFixedThreadPool(1, new TimeBasedRunnerThreadFactory(economy, duration));
    }

    private class TimerBasedTask implements Runnable {
        private final IEconomy economy;
        private final Duration duration;
        private final TimerHelper timerHelper = new TimerHelper();

        TimerBasedTask(IEconomy economy, Duration duration) {
            this.economy = economy;
            this.duration = duration;
        }

        @Override
        public void run() {
            LOG.info("Session started for {}s.", duration.toSeconds());
            timerHelper.start();
            Instant end = Instant.now().plus(duration);
            while (true) {
                economy.startSimulation();
                if (Instant.now().isAfter(end)) {
                    executorService.shutdown();
                    timerHelper.stop();
                    LOG.info("Finished runner in {}s", timerHelper.getTimeSeconds());
                    break;
                }
            }
        }
    }

    private static class TimeBasedRunnerThreadFactory implements ThreadFactory {
        private final Duration duration;
        private final IEconomy economy;

        TimeBasedRunnerThreadFactory(IEconomy economy, Duration duration) {
            this.duration = duration;
            this.economy = economy;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, duration.toSeconds() + "s-Time-" + economy.toString());
        }
    }

    @Override
    public void run() {
        try {
            executorService.submit(new TimerBasedTask(economy, duration)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runAsync() {
        executorService.submit(new TimerBasedTask(economy, duration));
    }
}
