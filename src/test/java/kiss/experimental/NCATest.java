/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.experimental;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @version 2011/03/22 17:11:06
 */
public class NCATest {

    /**
     * <p>
     * Retrieve the nearest common ancestor class of the given classes.
     * </p>
     * 
     * @param <X> A type.
     * @param classes A set of classes.
     * @return A nearest common ancestor class.
     */
    public static <X> Class getNCA(X... classes) {
        return classes.getClass().getComponentType();
    }

    @Test
    public void testname() throws Exception {
        assert getNCA(1, 2d, 3f).equals(Number.class);
        assert getNCA("test").equals(String.class);
        assert getNCA(new IOException(), new SAXException()).equals(Exception.class);

        // Class AbstractStringBuilder = getNCA(new StringBuffer(), new StringBuilder());
    }

}