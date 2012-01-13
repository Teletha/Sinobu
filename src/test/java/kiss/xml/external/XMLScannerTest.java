/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.xml.external;

import static org.junit.Assert.assertNotNull;


import org.junit.Test;

import kiss.xml.XMLScanner;

/**
 * DOCUMENT.
 * 
 * @version 2008/11/22 4:02:40
 */
public class XMLScannerTest {

    /**
     * Private rule class. (Out of {@link XMLScanner} package)
     */
    @Test
    public void testPrivateClass() throws Exception {
        assertNotNull(new PrivateRuleScanner());
    }

    /**
     * DOCUMENT.
     * 
     * @version 2008/11/22 4:03:45
     */
    protected static class PrivateRuleScanner extends XMLScanner {

        public static final String XMLNS = "test";
    }
}