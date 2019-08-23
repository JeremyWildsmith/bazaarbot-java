package com.bazaarbot;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * @author Nick Gritsenko
 */
public class TimerHelper {

    private Instant start;
    private Instant stop;


    public void start() {
        this.start = Instant.now();
    }

    public void stop() {
        this.stop = Instant.now();
    }

    private long getTime(TemporalUnit unit) {
        if (start == null || stop == null) {
            throw new RuntimeException("Cannot get time, start or stop point is null");
        }
        return Duration.between(start, stop).get(unit);
    }

    public long getTimeSeconds() {
        return getTime(ChronoUnit.SECONDS);
    }

    public long getTimeNanos() {
        return getTime(ChronoUnit.NANOS);
    }

}
