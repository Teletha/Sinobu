/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package filer;

import java.nio.file.Path;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import antibug.CleanRoom;

/**
 * @version 2015/07/14 2:39:52
 */
public class WalkTest {

    @Rule
    @ClassRule
    public static final CleanRoom room = new CleanRoom();

    @Test
    public void zip() {
        Path root = room.locateArchive("zip", $ -> {
            $.file("text1");
            $.file("text2");
            $.dir("dir1", () -> {
                $.file("text1");
                $.file("text2");
            });
            $.dir("dir2", () -> {
                $.file("text1");
                $.file("text2");
            });
        });

        assert Filer.walk(root).size() == 6;
        assert Filer.walk(root, "*").size() == 2;
        assert Filer.walk(root, "!dir1/**").size() == 4;
        assert Filer.walkDirectory(root).size() == 2;
    }
}