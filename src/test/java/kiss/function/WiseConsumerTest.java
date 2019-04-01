/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.function;

import org.junit.jupiter.api.Test;

import kiss.Variable;
import kiss.WiseBiConsumer;
import kiss.WiseConsumer;
import kiss.WiseRunnable;

class WiseConsumerTest {

    String value = null;

    WiseConsumer<String> setter = v -> value = v;

    @Test
    void narrowHead() {
        setter.hide("fixed").run();
        assert value.equals("fixed");
    }

    @Test
    void narrowHeadNull() {
        setter.hide(null).run();
        assert value == null;
    }

    @Test
    void narrowHeadLazily() {
        Variable<String> variable = Variable.of("init");
        WiseRunnable created = setter.hideBy(variable);

        created.run();
        assert value.equals("init");
        variable.set("change");
        created.run();
        assert value.equals("change");
    }

    @Test
    void narrowHeadLazilyNull() {
        setter.hideBy(null).run();
        assert value == null;
    }

    @Test
    void narrowTail() {
        setter.dump("fixed").run();
        assert value.equals("fixed");
    }

    @Test
    void narrowTailNull() {
        setter.dump(null).run();
        assert value == null;
    }

    @Test
    void narrowTailLazily() {
        Variable<String> variable = Variable.of("init");
        WiseRunnable created = setter.dumpBy(variable);

        created.run();
        assert value.equals("init");
        variable.set("change");
        created.run();
        assert value.equals("change");
    }

    @Test
    void narrowTailLazilyNull() {
        setter.dumpBy(null).run();
        assert value == null;
    }

    @Test
    void widenHead() {
        WiseBiConsumer<String, String> created = setter.prepend();
        created.accept("ignore", "use");
        assert value.equals("use");
    }

    @Test
    void widenHeadNull() {
        WiseBiConsumer<String, String> created = setter.prepend();
        created.accept(null, null);
        assert value == null;
    }

    @Test
    void widenTail() {
        WiseBiConsumer<String, String> created = setter.append();
        created.accept("use", "ignore");
        assert value.equals("use");
    }

    @Test
    void widenTailNull() {
        WiseBiConsumer<String, String> created = setter.append();
        created.accept(null, null);
        assert value == null;
    }
}
