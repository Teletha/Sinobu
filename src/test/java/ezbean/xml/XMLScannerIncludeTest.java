/*
 * Copyright (C) 2011 Nameless Production Committee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ezbean.xml;

import static ezunit.Ezunit.*;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version 2011/04/14 10:36:03
 */
public class XMLScannerIncludeTest {

    @Test
    public void path() throws Exception {
        XMLScanner scanner = new XMLScanner() {

            @SuppressWarnings("unused")
            @Rule(match = "root")
            public void in() {
                start("root");
                include(locate("include/pathIncluded.xml"));
                end();
            }
        };

        assertXMLIdentical("include/pathExpected.xml", "include/path.xml", scanner);
    }

    @Test
    public void pathWithFilter() throws Exception {
        XMLScanner scanner = new XMLScanner() {

            @SuppressWarnings("unused")
            @Rule(match = "root")
            public void in() {
                start("root");
                include(locate("include/pathIncluded.xml"), new StripChildElement());
                end();
            }
        };

        assertXMLIdentical("include/pathWithFilterExpected.xml", "include/path.xml", scanner);
    }

    @Test
    public void bit() throws Exception {
        XMLScanner scanner = new XMLScanner() {

            Bits bits;

            @SuppressWarnings("unused")
            @Rule(match = "from")
            public void from(Bits bits) {
                this.bits = bits;

                element("from");
            }

            @SuppressWarnings("unused")
            @Rule(match = "to")
            public void to() {
                start("to");
                include(bits);
                end();
            }
        };

        assertXMLIdentical("include/bitsExpected.xml", "include/bits.xml", scanner);
    }

    @Test
    public void bitWithProceed() throws Exception {
        XMLScanner scanner = new XMLScanner() {

            Bits bits;

            @SuppressWarnings("unused")
            @Rule(match = "from")
            public void from(Bits bits) {
                this.bits = bits;

                start("from");
                proceed();
                end();
            }

            @SuppressWarnings("unused")
            @Rule(match = "to")
            public void to() {
                start("to");
                include(bits);
                end();
            }
        };

        assertXMLIdentical("include/bitsProceedExpected.xml", "include/bits.xml", scanner);
    }

    /**
     * @version 2011/04/15 13:45:43
     */
    private static class StripChildElement extends XMLScanner {

        /**
         * @see org.xml.sax.helpers.XMLFilterImpl#startElement(java.lang.String, java.lang.String,
         *      java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if (!localName.equals("child")) {
                super.startElement(uri, localName, qName, atts);
            }
        }

        /**
         * @see org.xml.sax.helpers.XMLFilterImpl#endElement(java.lang.String, java.lang.String,
         *      java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (!localName.equals("child")) {
                super.endElement(uri, localName, qName);
            }
        }
    }
}