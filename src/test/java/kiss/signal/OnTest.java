/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package kiss.signal;

import static java.util.concurrent.TimeUnit.*;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import kiss.I;

/**
 * @version 2018/09/01 11:59:24
 */
class OnTest extends SignalTester {

    @Test
    void on() {
        monitor(1, signal -> signal.on(after20ms).map(v -> Thread.currentThread().getName().contains("ForkJoinPool")));

        main.emit("START");
        assert main.value();
        assert await(40).value(true);
    }

    @Test
    void error() {
        monitor(signal -> signal.on(after20ms).map(v -> Thread.currentThread().getName()));

        main.emit(Error.class);
        assert main.isNotError();
        assert main.isNotDisposed();
        await(40);
        assert main.isError();
        assert main.isDisposed();
    }

    @Test
    void complete() {
        monitor(signal -> signal.on(after20ms).map(v -> Thread.currentThread().getName()));

        main.emit(Complete);
        assert main.isNotCompleted();
        await(40);
        assert main.isCompleted();
    }

    @Test
    void dispose() {
        monitor(signal -> signal.take(1).on(after20ms));

        assert main.emit("First value will be accepted", "Second will not!").value();
        assert await(40).size(1);
        assert main.isCompleted();
        assert main.isNotError();
        assert main.isDisposed();
    }

    @Test
    void ignoreNull() {
        monitor(signal -> signal.on(null));

        assert main.emit("ignore").value("ignore");
        assert main.isNotCompleted();
        assert main.isNotError();
        assert main.isNotDisposed();
    }

    /**
     * Scheduler.
     */
    private Consumer<Runnable> after20ms = runner -> {
        I.schedule(20, ms, true, runner);
    };

    @Test
    void syncComplete() {
        monitor(1, () -> signal(50, 30, 10).on(task -> {
            System.out.println("task start  " + Thread.currentThread());
            I.schedule(10, MILLISECONDS, true, task);
        }).map(v -> {
            Thread.sleep(1000);
            System.out.println(v + "  " + Thread.currentThread());
            return v * 2;
        }));

        assert main.value();
        assert main.isNotCompleted();
        assert await(1500).value(100, 60, 20);
        assert main.isCompleted();
    }
}
