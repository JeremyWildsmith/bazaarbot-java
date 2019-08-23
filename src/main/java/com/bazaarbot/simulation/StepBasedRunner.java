package com.bazaarbot.simulation;

import com.bazaarbot.Economy2;
import com.bazaarbot.TimerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Nick Gritsenko
 */
public class StepBasedRunner {
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

    private final Economy2 economy;
    private final int rounds;

    public StepBasedRunner(Economy2 economy, int rounds) {
        this.economy = economy;
        this.rounds = rounds;
    }

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
            LOG.error("Error while doing simulation", e);
        }

        timerHelper.stop();
        LOG.info("Finished simulation in {}s", timerHelper.getTimeSeconds());
        executorService.shutdownNow();
    }
}
