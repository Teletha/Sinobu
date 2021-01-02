/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package kiss.signal;

import org.junit.jupiter.api.Test;

class IndexText extends SignalTester {

    @Test
    void index() {
        monitor(String.class, signal -> signal.index().map(v -> v.ⅰ + v.ⅱ));

        assert main.emit("A").value("A0");
        assert main.emit("B").value("B1");
        assert main.emit("C").value("C2");
        assert main.emit(Complete).isCompleted();
    }

    @Test
    void start() {
        monitor(String.class, signal -> signal.index(10).map(v -> v.ⅰ + v.ⅱ));

        assert main.emit("A").value("A10");
        assert main.emit("B").value("B11");
        assert main.emit("C").value("C12");
        assert main.emit(Complete).isCompleted();
    }
}