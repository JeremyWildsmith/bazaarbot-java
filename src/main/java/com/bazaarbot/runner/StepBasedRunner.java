package com.bazaarbot.runner;

import com.bazaarbot.TimerHelper;
import com.bazaarbot.economy.IEconomy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Nick Gritsenko
 */
public class StepBasedRunner implements IRunner {
    private static final Logger LOG = LoggerFactory.getLogger(StepBasedRunner.class);
    private final TimerHelper timerHelper = new TimerHelper();

    private class RoundBasedRunnerThreadFactory implements ThreadFactory {
        private final int steps;

        RoundBasedRunnerThreadFactory(int steps) {
            this.steps = steps;
        }

        public Thread newThread(Runnable r) {
            return new Thread(r, steps + "-StepsBasedRunner");
        }
    }

    private final IEconomy economy;
    private final int rounds;

    public StepBasedRunner(IEconomy economy, int rounds) {
        this.economy = economy;
        this.rounds = rounds;
    }

    @Override
    public void run() {
        LOG.info("Session started with {} steps", rounds);
        timerHelper.start();
        ExecutorService executorService = Executors.newSingleThreadExecutor(new RoundBasedRunnerThreadFactory(rounds));
        try {
            executorService.submit(() -> {
                for (int i = 0; i < rounds; i++) {
                    economy.startSimulation();
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Error while doing runner", e);
        }

        timerHelper.stop();
        LOG.info("Finished runner in {}s", timerHelper.getTimeSeconds());
        executorService.shutdownNow();
    }
}
