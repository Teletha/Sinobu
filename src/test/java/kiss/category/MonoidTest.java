/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.category;

import java.util.Arrays;

import org.junit.Test;

import kiss.I;

/**
 * @version 2016/03/29 14:28:10
 */
public class MonoidTest {

    static {
        I.load(MonoidTest.class, true);
    }

    @Test
    public void use() throws Exception {
        assert twice(2) == 4;
        assert twice(3) == 6;
        assert twice(1.2) == 2.4;
        assert twice("1").equals("11");
        assert twice(Arrays.asList("A", "B")).equals(Arrays.asList("A", "B", "A", "B"));
    }

    public <A> A twice(A value) {
        Monoid<A> monoid = I.find(Monoid.class, value.getClass());

        return monoid.append(value, value);
    }
}