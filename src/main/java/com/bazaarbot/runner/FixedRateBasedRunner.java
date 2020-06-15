package com.bazaarbot.runner;

import com.bazaarbot.TimerHelper;
import com.bazaarbot.economy.IEconomy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Nick Gritsenko
 */
public class FixedRateBasedRunner implements IRunner {
    private static final Logger LOG = LoggerFactory.getLogger(FixedRateBasedRunner.class);

    private static class RoundBasedRunnerThreadFactory implements ThreadFactory {
        private final Duration fixedRate;

        RoundBasedRunnerThreadFactory(Duration fixedRate) {
            this.fixedRate = fixedRate;
        }

        public Thread newThread(Runnable r) {
            return new Thread(r, fixedRate + "-FixedRateBasedRunner");
        }
    }

    private static class StepBasedTask implements Runnable {
        private final IEconomy economy;
        private final Duration fixedRate;
        private final TimerHelper timerHelper = new TimerHelper();

        StepBasedTask(IEconomy economy, Duration fixedRate) {
            this.economy = economy;
            this.fixedRate = fixedRate;
        }

        @Override
        public void run() {
            LOG.info("Session started with fixed rate of {}.", fixedRate);
            timerHelper.start();
            Instant next = Instant.now().plus(fixedRate);
            while (true) {
                if (Instant.now().isAfter(next)) {
                    economy.startSimulation();
                }
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            timerHelper.stop();
            LOG.info("Finished runner in {}s", timerHelper.getTimeSeconds());
        }
    }

    private final IEconomy economy;
    private final Duration fixedRate;
    private final ExecutorService executorService;

    public FixedRateBasedRunner(IEconomy economy, Duration fixedRate) {
        this.economy = economy;
        this.fixedRate = fixedRate;
        this.executorService = Executors.newSingleThreadExecutor(new RoundBasedRunnerThreadFactory(fixedRate));
    }

    @Override
    public void run() {
        StepBasedTask task = new StepBasedTask(economy, fixedRate);
        task.run();
    }

    @Override
    public void runAsync() {
        executorService.submit(new StepBasedTask(economy, fixedRate));
        executorService.shutdown();
    }
}
