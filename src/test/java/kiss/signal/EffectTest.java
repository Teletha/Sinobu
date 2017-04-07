/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.signal;

import org.junit.Test;

import kiss.I;
import kiss.Signal;

/**
 * @version 2017/04/07 15:46:56
 */
public class EffectTest extends SignalTestBase {

    @Test
    public void effect() {
        monitor(signal -> signal.effect(log1));

        assert emit(1).value(1);
        assert log1.value(1);
        assert emit(2, 3).value(2, 3);
        assert log1.value(2, 3);
    }

    @Test
    public void effectNull() {
        Signal<Integer> from = I.signal(0);
        assert from == from.effect(null);
        assert from == from.effectOnComplete((Runnable) null);
        assert from == from.effectOnError(null);
    }

    @Test
    public void effectOnComplet() throws Exception {
        monitor(signal -> signal.effectOnComplete(log1::complete));

        assert log1.isNotCompleted();
        emit(Complete);
        assert log1.isCompleted();
    }

    @Test
    public void effectOnError() throws Exception {
        monitor(signal -> signal.effectOnError(log1::error));

        assert log1.isNotError();
        emit(Error);
        assert log1.isError();
    }
}