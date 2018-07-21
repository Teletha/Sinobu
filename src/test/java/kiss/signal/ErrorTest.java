/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.signal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import kiss.I;

/**
 * @version 2018/07/20 18:58:16
 */
class ErrorTest extends SignalTester {

    @Test
    void errorInSubscriber() {
        List<Integer> values = new ArrayList();
        AtomicReference<Throwable> error = new AtomicReference();
        AtomicInteger complete = new AtomicInteger();

        I.signal(1, 2, 3, 4, 5).to(value -> {
            values.add(value);

            if (value == 3) {
                throw new IllegalStateException();
            }
        }, error::set, complete::incrementAndGet);

        assert values.size() == 3;
        assert error.get() instanceof IllegalStateException;
        assert complete.get() == 0;
    }

    @Test
    void errorInSubscriberSignal() {
        List<Integer> values = new ArrayList();
        AtomicReference<Throwable> error = new AtomicReference();
        AtomicInteger complete = new AtomicInteger();

        I.signal(1, 2, 3).to(value -> {
            I.signal(value).map(v -> v + 1).to(v -> {
                values.add(v);

                if (v == 3) {
                    throw new IllegalStateException(String.valueOf(v));
                }
            });
        }, error::set, complete::incrementAndGet);

        assert values.size() == 2;
        assert error.get() instanceof IllegalStateException;
        assert complete.get() == 0;
    }
}