package com.bazaarbot;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author Nick Gritsenko
 */
public class TestRunner {

    public static void main(String[] args) throws Exception {

        Options opt = new OptionsBuilder()
                .include("com.bazaarbot.performance.*")
                //.addProfiler(StackProfiler.class)
                .forks(1).jvmArgsAppend("-Xms2048m", "-Xmx2048m", "-XX:+UseG1GC")
                .warmupIterations(5)
                .measurementIterations(20)
                .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }
}
