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

import kiss.SignalTester;

/**
 * @version 2018/03/03 19:40:55
 */
public class IsEmptyTest extends SignalTester {

    @Test
    public void value() {
        monitor(Object.class, Boolean.class, signal -> signal.isEmpty());

        assert main.emit(1, 2, 3).value(false);
        assert main.isCompleted();
        assert main.isDisposed();
    }

    @Test
    public void error() {
        monitor(Object.class, Boolean.class, signal -> signal.isEmpty());

        assert main.emit(Error.class).value(false);
        assert main.isCompleted();
        assert main.isDisposed();
    }

    @Test
    public void complete() {
        monitor(Object.class, Boolean.class, signal -> signal.isEmpty());

        assert main.emit(Complete).value(true);
        assert main.isCompleted();
        assert main.isDisposed();
    }
}
