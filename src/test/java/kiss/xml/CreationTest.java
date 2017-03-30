/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.xml;

import java.io.ByteArrayInputStream;
import java.io.StringReader;

import org.junit.Test;

import kiss.I;
import kiss.XML;

/**
 * @version 2017/03/30 16:50:34
 */
public class CreationTest {

    @Test
    public void elementName() throws Exception {
        XML xml = I.xml("test");
        assert xml.size() == 1;
        assert xml.name() == "test";
    }

    @Test
    public void xmlLiteral() throws Exception {
        XML xml = I.xml("<test/>");
        assert xml.size() == 1;
        assert xml.name() == "test";
    }

    @Test
    public void htmlLiteral() throws Exception {
        XML xml = I.xml("<html/>");
        assert xml.size() == 1;
        assert xml.name() == "html";
    }

    @Test
    public void inputStream() throws Exception {
        XML xml = I.xml(new ByteArrayInputStream("<html/>".getBytes()));
        assert xml.size() == 1;
        assert xml.name() == "html";
    }

    @Test
    public void reader() throws Exception {
        XML xml = I.xml(new StringReader("<html/>"));
        assert xml.size() == 1;
        assert xml.name() == "html";
    }

    @Test(expected = NullPointerException.class)
    public void Null() throws Exception {
        I.xml(null);
    }
}
