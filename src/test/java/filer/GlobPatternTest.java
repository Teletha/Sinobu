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

import org.junit.Rule;
import org.junit.Test;

import antibug.CleanRoom;

/**
 * @version 2016/01/20 11:49:55
 */
public class GlobPatternTest {

    @Rule
    public CleanRoom room = new CleanRoom();

    @Test
    public void topLevelWildcard() {
        Path root = room.locateDirectory("root", $ -> {
            $.file("file");
            $.file("text");
            $.dir("dir", () -> {
                $.file("file");
                $.file("text");
            });
        });
        assert Filer.walk(root, "*").size() == 2;
        assert Filer.walk(root, "*/text").size() == 1;
        assert Filer.walk(root, "*", "*/text").size() == 3;
    }

    @Test
    public void secondLevelWildcard() {
        Path root = room.locateDirectory("root", $ -> {
            $.file("file");
            $.dir("dir1", () -> {
                $.file("file1");
                $.file("file2");
                $.file("text1");
            });
            $.dir("dir2", () -> {
                $.file("file1");
                $.file("file2");
                $.file("text1");
            });
        });
        assert Filer.walk(root, "*/*").size() == 6;
        assert Filer.walk(root, "*/file*").size() == 4;
        assert Filer.walk(root, "*/*1").size() == 4;
    }

    @Test
    public void character() {
        Path root = room.locateDirectory("root", $ -> {
            $.file("text1");
            $.file("text2");
            $.dir("dir", () -> {
                $.file("text1");
                $.file("text2");
            });
        });
        assert Filer.walk(root, "text?").size() == 2;
        assert Filer.walk(root, "????1").size() == 1;
        assert Filer.walk(root, "**text?").size() == 4;
    }

    @Test
    public void range() {
        Path root = room.locateDirectory("root", $ -> {
            $.file("text1");
            $.file("text2");
            $.file("text3");
            $.file("text4");
        });
        assert Filer.walk(root, "text[1-2]").size() == 2;
        assert Filer.walk(root, "text[2-5]").size() == 3;
    }

    @Test
    public void negate() {
        Path root = room.locateDirectory("root", $ -> {
            $.file("text1");
            $.file("text2");
            $.file("text3");
            $.file("text4");
        });
        assert Filer.walk(root, "text[!3]").size() == 3;
        assert Filer.walk(root, "text[!34]").size() == 2;
    }

    @Test
    public void multiple() {
        Path root = room.locateDirectory("root", $ -> {
            $.file("text1");
            $.file("text2");
            $.file("text3");
            $.file("text4");
        });
        assert Filer.walk(root, "**", "!**1", "!**3").size() == 2;
    }
}