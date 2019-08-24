package com.bazaarbot.runner;

import com.bazaarbot.TimerHelper;
import com.bazaarbot.economy.IEconomy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * @author Nick Gritsenko
 */
public class TimeBasedRunner implements IRunner {
    private static final Logger LOG = LoggerFactory.getLogger(TimeBasedRunner.class);
    private final TimerHelper timerHelper = new TimerHelper();
    private final ForkJoinPool threadPool = new ForkJoinPool(1);

    private final IEconomy economy;
    private final Duration duration;
    private final long tick;

    public TimeBasedRunner(IEconomy economy, Duration simulationDuration) {
        this(economy, simulationDuration, 0);
    }

    public TimeBasedRunner(IEconomy economy, Duration simulationDuration, long tick) {
        this.economy = economy;
        this.duration = simulationDuration;
        this.tick = tick;
    }

    private class TimerBasedTask extends TimerTask {
        private final IEconomy economy;

        TimerBasedTask(IEconomy economy) {
            this.economy = economy;
        }

        @Override
        public void run() {
            economy.startSimulation();
        }
    }

    private class TimeBasedRunnerThreadFactory implements ThreadFactory {
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

    private void timerBasedRun() {
        Timer timer = new Timer(tick + "Time-" + economy.toString());
        timer.schedule(new TimerBasedTask(economy), 0, tick);
        Instant end = Instant.now().plus(duration);
        while (true) {
            if (Instant.now().isAfter(end)) {
                timer.cancel();
                break;
            }
        }
    }

    private void timeBasedRun() {
        ExecutorService executorService = Executors.newFixedThreadPool(1, new TimeBasedRunnerThreadFactory(economy, duration));
        Instant end = Instant.now().plus(duration);
        while (true) {
            try {
                executorService.submit(economy::startSimulation).get();
            } catch (InterruptedException | ExecutionException e) {
                LOG.error("Error while simulating.", e);
                break;
            }
            if (Instant.now().isAfter(end)) {
                break;
            }
        }
        executorService.shutdownNow();
    }

    @Override
    public void run() {
        LOG.info("Session started for {}s.", duration.toSeconds());
        timerHelper.start();
        if (tick != 0) {
            LOG.info("Simulation is running each {}ms", tick);
            timerBasedRun();
        } else {
            timeBasedRun();
        }
        timerHelper.stop();
        LOG.info("Finished runner in {}s", timerHelper.getTimeSeconds());
    }
}
