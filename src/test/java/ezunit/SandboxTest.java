/*
 * Copyright (C) 2011 Nameless Production Committee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ezunit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.AccessControlException;

import org.junit.Rule;
import org.junit.Test;

import ezbean.I;
import ezbean.io.FileSystem;

/**
 * @version 2010/02/09 11:23:23
 */
public class SandboxTest {

    @Rule
    public static Sandbox sandbox = new Sandbox(Sandbox.READ);

    @Test
    public void read1() throws Exception {
        sandbox.readable(true);

        new FileReader(new File("pom.xml"));
    }

    @Test(expected = AccessControlException.class)
    public void read2() throws Exception {
        new FileReader(new File("pom.xml"));
    }

    @Test
    public void writableFile() throws Exception {
        File file = FileSystem.createTemporary();

        sandbox.writable(false, file);

        // try to write
        FileWriter writer = null;

        try {
            writer = new FileWriter(file);

            fail("This is writable file.");
        } catch (AccessControlException e) {
            // success
        }

        // make writable
        sandbox.writable(true, file);

        try {
            writer = new FileWriter(file);
        } catch (AccessControlException e) {
            fail("This is unwritable file.");
        } finally {
            I.quiet(writer);
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void writableDirectory() throws Exception {
        File file = FileSystem.createTemporary();

        sandbox.writable(false, file);

        // try to write
        FileWriter writer = null;

        try {
            writer = new FileWriter(new File(file, "file"));

            fail("This is writable file.");
        } catch (AccessControlException e) {
            // success
        }

        // make writable
        sandbox.writable(true, file);

        try {
            writer = new FileWriter(new File(file, "file"));
        } catch (AccessControlException e) {
            fail("This is unwritable file.");
        } finally {
            I.quiet(writer);
        }
    }
}
