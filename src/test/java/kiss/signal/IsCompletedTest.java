/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package kiss.signal;

import org.junit.Test;

/**
 * @version 2018/03/02 13:36:05
 */
public class IsCompletedTest extends SignalTester {

    @Test
    public void value() {
        monitor(Object.class, Boolean.class, signal -> signal.isCompleted());

        assert main.emit(1, 2, 3).value();
        assert main.isNotCompleted();
        assert main.isNotDisposed();
    }

    @Test
    public void error() {
        monitor(Object.class, Boolean.class, signal -> signal.isCompleted());

        assert main.emit(1, 2, 3).value();
        assert main.emit(Error.class).value();
        assert main.isNotCompleted();
        assert main.isDisposed();
    }

    @Test
    public void complete() {
        monitor(Object.class, Boolean.class, signal -> signal.isCompleted());

        assert main.emit(1, 2, 3).value();
        assert main.emit(Complete).value(true);
        assert main.isCompleted();
        assert main.isDisposed();
    }
}
