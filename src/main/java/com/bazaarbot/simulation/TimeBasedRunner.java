package com.bazaarbot.simulation;

import com.bazaarbot.Economy;
import com.bazaarbot.TimerHelper;
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
public class TimeBasedRunner {
    private static final Logger LOG = LoggerFactory.getLogger(TimeBasedRunner.class);
    private final TimerHelper timerHelper = new TimerHelper();
    private final ForkJoinPool threadPool = new ForkJoinPool(1);

    private final Economy economy;
    private final Duration duration;
    private final long tick;

    public TimeBasedRunner(Economy economy, Duration simulationDuration) {
        this(economy, simulationDuration, 0);
    }

    public TimeBasedRunner(Economy economy, Duration simulationDuration, long tick) {
        this.economy = economy;
        this.duration = simulationDuration;
        this.tick = tick;
    }

    private class TimerBasedTask extends TimerTask {
        private final Economy economy;

        TimerBasedTask(Economy economy) {
            this.economy = economy;
        }

        @Override
        public void run() {
            economy.startSimulation();
        }
    }

    private class TimeBasedRunnerThreadFactory implements ThreadFactory {
        private final Duration duration;
        private final Economy economy;

        TimeBasedRunnerThreadFactory(Economy economy, Duration duration) {
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
        LOG.info("Finished simulation in {}s", timerHelper.getTimeSeconds());
    }
}
