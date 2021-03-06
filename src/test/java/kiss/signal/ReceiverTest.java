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

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import kiss.I;
import kiss.Variable;

class ReceiverTest extends SignalTester {

    @Test
    void to() {
        monitor(signal -> signal);

        assert main.emit(1).value(1);
        assert main.emit(2).value(2);
        assert main.isNotDisposed();
    }

    @Test
    void toCollection() {
        ArrayDeque<Integer> set = I.signal(30, 20, 10).toCollection(new ArrayDeque<Integer>(100));
        Iterator<Integer> iterator = set.iterator();
        assert iterator.next() == 30;
        assert iterator.next() == 20;
        assert iterator.next() == 10;
    }

    @Test
    void toCollectionType() {
        LinkedHashSet<Integer> set = I.signal(30, 20, 10).to(LinkedHashSet.class);
        Iterator<Integer> iterator = set.iterator();
        assert iterator.next() == 30;
        assert iterator.next() == 20;
        assert iterator.next() == 10;
    }

    @Test
    void toAlternate() {
        Set<Integer> set = I.signal(30, 20, 10).toAlternate();
        assert set.contains(10);
        assert set.contains(20);
        assert set.contains(30);

        // duplicate
        set = I.signal(30, 20, 20, 30).toAlternate();
        assert set.isEmpty();

        // triple
        set = I.signal(30, 20, 20, 30, 10, 20).toAlternate();
        assert set.contains(10);
        assert set.contains(20);
    }

    @Test
    void toBinary() {
        Variable<Boolean> binary = I.signal().toBinary();
        assert binary.is(false);

        binary = I.signal("on").toBinary();
        assert binary.is(true);

        binary = I.signal("on", "off").toBinary();
        assert binary.is(false);

        binary = I.signal("on", "off", "on again").toBinary();
        assert binary.is(true);
    }

    @Test
    void toList() {
        List<String> list = I.<String> signal().toList();
        assert list.isEmpty();

        list = I.signal("A").toList();
        assert list.get(0) == "A";

        list = I.signal("A", "B").toList();
        assert list.get(0) == "A";
        assert list.get(1) == "B";

        list = I.signal("A", "B", "C").toList();
        assert list.get(0) == "A";
        assert list.get(1) == "B";
        assert list.get(2) == "C";
    }

    @Test
    void toMap() {
        Map<String, String> map = I.<String> signal().toMap(v -> "KEY-" + v);
        assert map.isEmpty();

        map = I.signal("A").toMap(v -> "KEY-" + v);
        assert map.get("KEY-A") == "A";

        map = I.signal("A", "B").toMap(v -> "KEY-" + v);
        assert map.get("KEY-B") == "B";
        assert map.size() == 2;

        map = I.signal("A", "B", "A").toMap(v -> "KEY-" + v);
        assert map.size() == 2;
    }

    @Test
    void toSet() {
        Set<String> set = I.<String> signal().toSet();
        assert set.isEmpty();

        set = I.signal("A").toSet();
        assert set.size() == 1;

        set = I.signal("A", "B").toSet();
        assert set.size() == 2;

        set = I.signal("A", "B", "A").toSet();
        assert set.size() == 2;
    }

    @Test
    void toGroup() {
        Map<Integer, List<String>> map = I.<String> signal().toGroup(String::length);
        assert map.isEmpty();

        map = I.signal("A", "BC", "DE", "F", "GHI", "JKLN").toGroup(String::length);
        assert map.get(1).size() == 2;
        assert map.get(2).size() == 2;
        assert map.get(3).size() == 1;
        assert map.get(4).size() == 1;
    }
}