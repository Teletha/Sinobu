/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.file;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.function.BiPredicate;

import org.junit.Rule;
import org.junit.Test;

import antibug.CleanRoom;
import kiss.I;

/**
 * @version 2015/06/24 10:04:35
 */
public class CopyTest extends PathOperationTestHelper {

    @Rule
    public CleanRoom room = new CleanRoom();

    /**
     * <p>
     * Test operation.
     * </p>
     * 
     * @param one
     * @param other
     */
    private void operate(Path one, Path other, String... patterns) {
        I.copy(one, other, patterns);
    }

    /**
     * <p>
     * Test operation.
     * </p>
     * 
     * @param one
     * @param other
     */
    private void operate(Path one, Path other, BiPredicate<Path, BasicFileAttributes> filter) {
        I.copy(one, other, filter);
    }

    @Test(expected = NullPointerException.class)
    public void nullInput() throws Exception {
        Path in = null;
        Path out = room.locateAbsent("null");

        operate(in, out);
    }

    @Test(expected = NullPointerException.class)
    public void nullOutput() throws Exception {
        Path in = room.locateAbsent("null");
        Path out = null;

        operate(in, out);
    }

    @Test
    public void absentToFile() throws Exception {
        Path in = room.locateAbsent("absent");
        Path out = room.locateFile("out");

        operate(in, out);

        assert notExist(in);
        assert exist(out);
    }

    @Test
    public void absentToDirectory() throws Exception {
        Path in = room.locateAbsent("absent");
        Path out = room.locateDirectory("out");

        operate(in, out);

        assert notExist(in);
        assert exist(out);
    }

    @Test
    public void absentToAbsent() throws Exception {
        Path in = room.locateAbsent("absent");
        Path out = room.locateAbsent("out");

        operate(in, out);

        assert notExist(in);
        assert notExist(out);
    }

    @Test
    public void fileToFile() throws Exception {
        Path in = room.locateFile("In", "Success");
        Path out = room.locateFile("Out", "This text will be overwritten by input file.");

        operate(in, out);

        assert sameFile(in, out);
    }

    @Test
    public void fileToFileWithSameTimeStamp() throws Exception {
        Instant now = Instant.now();
        Path in = room.locateFile("In", now, "Success");
        Path out = room.locateFile("Out", now, "This text will be overwritten by input file.");

        operate(in, out);

        assert sameFile(in, out);
    }

    @Test
    public void fileToFileWithDifferentTimeStamp() {
        Instant now = Instant.now();
        Path in = room.locateFile("In", now, "Success");
        Path out = room.locateFile("Out", now.plusSeconds(10), "This text will be overwritten by input file.");

        operate(in, out);

        assert sameFile(in, out);
    }

    @Test
    public void fileToAbsent() {
        Path in = room.locateFile("In", "Success");
        Path out = room.locateAbsent("Out");
        operate(in, out);

        assert sameFile(in, out);
    }

    @Test
    public void fileToDeepAbsent() {
        Path in = room.locateFile("In", "Success");
        Path out = room.locateAbsent("1/2/3");
        operate(in, out);

        assert sameFile(in, out);
    }

    @Test
    public void fileToDirectory() {
        Path in = room.locateFile("In", "Success");
        Path out = room.locateDirectory("Out");

        operate(in, out);

        assert sameFile(in, out.resolve("In"));
    }

    @Test(expected = NoSuchFileException.class)
    public void directoryToFile() {
        Path in = room.locateDirectory("In", $ -> {
            $.file("1", "One");
        });
        Path out = room.locateFile("Out");

        operate(in, out);
    }

    @Test
    public void directoryToDirectory() {
        Path in = room.locateDirectory("In", $ -> {
            $.file("1", "One");
        });
        Path out = room.locateDirectory("Out", $ -> {
            $.file("1", "This text will be overwritten by input file.");
        });

        operate(in, out);

        assert sameDirectory(in, out.resolve("In"));
    }

    @Test
    public void directoryToDirectoryWithFilter() {
        Path in = room.locateDirectory("In", $ -> {
            $.file("file");
            $.file("text");
            $.dir("dir", () -> {
                $.file("file");
                $.file("text");
            });
        });
        Path out = room.locateDirectory("Out");

        operate(in, out, (file, attr) -> file.getFileName().startsWith("file"));

        assert sameFile(in.resolve("file"), out.resolve("In/file"));
        assert sameFile(in.resolve("dir/file"), out.resolve("In/dir/file"));
        assert notExist(out.resolve("In/text"), out.resolve("In/dir/text"));
    }

    @Test
    public void directoryToAbsent() {
        Path in = room.locateDirectory("In", $ -> {
            $.file("1", "One");
        });
        Path out = room.locateAbsent("Out");

        operate(in, out);

        assert sameDirectory(in, out.resolve("In"));
    }

    @Test
    public void directoryToDeepAbsent() {
        Path in = room.locateDirectory("In", $ -> {
            $.file("1", "One");
        });
        Path out = room.locateAbsent("1/2/3");

        operate(in, out);

        assert sameDirectory(in, out.resolve("In"));
    }

    @Test
    public void children() {
        Path in = room.locateDirectory("In", $ -> {
            $.file("file");
            $.file("text");
            $.dir("dir", () -> {
                $.file("file");
                $.file("text");
            });
        });
        Path out = room.locateDirectory("Out");

        operate(in, out, "*");

        assert exist(out.resolve("file"), out.resolve("text"));
        assert notExist(out.resolve("dir"), out.resolve("dir/file"), out.resolve("dir/text"));
    }

    @Test
    public void descendant() {
        Path in = room.locateDirectory("In", $ -> {
            $.file("1", "One");
            $.file("2", "Two");
            $.dir("dir", () -> {
                $.file("nest");
            });
        });
        Path out = room.locateDirectory("Out");

        operate(in, out, "**");

        assert sameDirectory(in, out);
    }
}
