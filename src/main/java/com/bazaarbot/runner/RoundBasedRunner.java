package com.bazaarbot.runner;

import com.bazaarbot.TimerHelper;
import com.bazaarbot.economy.IEconomy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Nick Gritsenko
 */
public class RoundBasedRunner implements IRunner {
    private static final Logger LOG = LoggerFactory.getLogger(RoundBasedRunner.class);

    private static class RoundBasedRunnerThreadFactory implements ThreadFactory {
        private final int steps;

        RoundBasedRunnerThreadFactory(int steps) {
            this.steps = steps;
        }

        public Thread newThread(Runnable r) {
            return new Thread(r, steps + "-StepsBasedRunner");
        }
    }

    private static class StepBasedTask implements Runnable {
        private final IEconomy economy;
        private final int rounds;
        private final TimerHelper timerHelper = new TimerHelper();

        StepBasedTask(IEconomy economy, int rounds) {
            this.economy = economy;
            this.rounds = rounds;
        }

        @Override
        public void run() {
            LOG.info("Session started for {} rounds.", rounds);
            timerHelper.start();
            for (int i = 0; i < rounds; i++) {
                economy.startSimulation();
            }
            timerHelper.stop();
            LOG.info("Finished runner in {}s", timerHelper.getTimeSeconds());
        }
    }

    private final IEconomy economy;
    private final int rounds;
    private final ExecutorService executorService;

    public RoundBasedRunner(IEconomy economy, int rounds) {
        this.economy = economy;
        this.rounds = rounds;
        this.executorService = Executors.newSingleThreadExecutor(new RoundBasedRunnerThreadFactory(rounds));
    }

    @Override
    public void run() {
        StepBasedTask task = new StepBasedTask(economy, rounds);
        task.run();
    }

    @Override
    public void runAsync() {
        executorService.submit(new StepBasedTask(economy, rounds));
        executorService.shutdown();
    }
}
