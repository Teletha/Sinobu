/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import kiss.I;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @version 2012/02/06 16:24:40
 */
public class Element implements Iterable<Element> {

    /**
     * Original pattern.
     * 
     * <pre>
     * ([>~+\- ])?(\w+|\*)?(#(\w+))?((\.\w+)*)(\[\s?(\w+)(\s?([=~^$*|])?=\s?["]([^"]*)["])?\s?\])?(:([\w-]+)(\(?(odd|even|(\d*)(n)?(\+(\d+))?|(.*))\))?)?
     * </pre>
     */
    private static final Pattern SELECTOR = Pattern.compile("([>~+\\- ])?(\\w+|\\*)?(#(\\w+))?((\\.\\w+)*)(\\[\\s?(\\w+)(\\s?([=~^$*|])?=\\s?[\"]([^\"]*)[\"])?\\s?\\])?(:([\\w-]+)(\\(?(odd|even|(\\d*)(n)?(\\+(\\d+))?|(.*))\\))?)?");

    /** The cache for compiled selectors. */
    private static final Map<String, XPathExpression> selectors = new ConcurrentHashMap();

    /** The document builder. */
    private static final DocumentBuilder dom;

    /** The xpath evaluator. */
    private static final XPath xpath;

    // initialization
    static {
        try {
            dom = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            xpath = XPathFactory.newInstance().newXPath();
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    public static final Element $(String xml) {
        return parse(xml);
    }

    /** The current document. */
    private Document doc;

    /** The current node set. */
    private final List<Node> nodes;

    /**
     * <p>
     * From node set.
     * </p>
     * 
     * @param nodes
     */
    private Element(Document doc, List<Node> nodes) {
        this.doc = doc;
        this.nodes = nodes;
    }

    /**
     * <p>
     * Insert content, specified by the parameter, to the end of each element in the set of matched
     * elements.
     * </p>
     * 
     * @param xml
     * @return
     */
    public Element append(Object xml) {
        Node n = copy(parse(xml));

        for (Node node : nodes) {
            node.appendChild(n.cloneNode(true));
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Insert content, specified by the parameter, to the beginning of each element in the set of
     * matched elements.
     * </p>
     * 
     * @param xml
     * @return
     */
    public Element prepend(Object xml) {
        Node n = copy(parse(xml));

        for (Node node : nodes) {
            node.insertBefore(n.cloneNode(true), node.getFirstChild());
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Insert content, specified by the parameter, before each element in the set of matched
     * elements.
     * </p>
     * 
     * @param xml
     * @return
     */
    public Element before(Object xml) {
        Node n = copy(parse(xml));

        for (Node node : nodes) {
            node.getParentNode().insertBefore(n.cloneNode(true), node);
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Insert content, specified by the parameter, after each element in the set of matched
     * elements.
     * </p>
     * 
     * @param xml
     * @return
     */
    public Element after(Object xml) {
        Node n = copy(parse(xml));

        for (Node node : nodes) {
            node.getParentNode().insertBefore(n.cloneNode(true), node.getNextSibling());
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Remove all child nodes of the set of matched elements from the DOM.
     * </p>
     * 
     * @return
     */
    public Element empty() {
        for (Node node : nodes) {
            while (node.hasChildNodes()) {
                node.removeChild(node.getFirstChild());
            }
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Remove the set of matched elements from the DOM.
     * </p>
     * <p>
     * Similar to {@link #empty()}, the {@link #remove()} method takes elements out of the DOM. Use
     * {@link #remove()} when you want to remove the element itself, as well as everything inside
     * it.
     * </p>
     * 
     * @return
     */
    public Element remove() {
        for (Node node : nodes) {
            node.getParentNode().removeChild(node);
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Wrap an HTML structure around each element in the set of matched elements.
     * </p>
     * 
     * @param xml
     * @return
     */
    public Element wrap(Object xml) {
        Element element = parse(xml);

        for (Element e : this) {
            e.wrapAll(element);
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Wrap an HTML structure around all elements in the set of matched elements.
     * </p>
     * 
     * @param xml
     * @return
     */
    public Element wrapAll(Object xml) {
        Element e = parse(xml);

        first().after(e).find(".+*").append(this);

        // API definition
        return this;
    }

    /**
     * <p>
     * Create a deep copy of the set of matched elements.
     * </p>
     * <p>
     * The .clone() method performs a deep copy of the set of matched elements, meaning that it
     * copies the matched elements as well as all of their descendant elements and text nodes. When
     * used in conjunction with one of the insertion methods, .clone() is a convenient way to
     * duplicate elements on a page.
     * </p>
     * {@inheritDoc}
     */
    public Element clone() {
        List<Node> list = new ArrayList();

        for (Node node : nodes) {
            list.add(node.cloneNode(true));
        }
        return new Element(doc, list);
    }

    /**
     * <p>
     * Get the combined text contents of each element in the set of matched elements, including
     * their descendants.
     * </p>
     * 
     * @return
     */
    public String text() {
        StringBuilder text = new StringBuilder();

        for (Node node : nodes) {
            text.append(node.getTextContent());
        }
        return text.toString();
    }

    /**
     * <p>
     * Set the content of each element in the set of matched elements to the specified text.
     * </p>
     * 
     * @param text
     * @return
     */
    public Element text(String text) {
        for (Node node : nodes) {
            node.setTextContent(text);
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Get the value of an attribute for the first element in the set of matched elements.
     * </p>
     * 
     * @param name An attribute name.
     * @return
     */
    public String attr(String name) {
        return ((org.w3c.dom.Element) nodes.iterator().next()).getAttribute(name);
    }

    /**
     * <p>
     * Set one or more attributes for the set of matched elements.
     * </p>
     * 
     * @param name
     * @param value
     * @return
     */
    public Element attr(String name, String value) {
        for (Node node : nodes) {
            if (value == null) {
                ((org.w3c.dom.Element) node).removeAttribute(name);
            } else {
                ((org.w3c.dom.Element) node).setAttribute(name, value);
            }
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Adds the specified class(es) to each of the set of matched elements.
     * </p>
     * <p>
     * It's important to note that this method does not replace a class. It simply adds the class,
     * appending it to any which may already be assigned to the elements. More than one class may be
     * added at a time, separated by a space, to the set of matched elements.
     * </p>
     * 
     * @param names
     * @return
     */
    public Element addClass(String names) {
        for (Element e : this) {
            String value = " ".concat(e.attr("class")).concat(" ");

            for (String name : names.split(" ")) {
                if (!value.contains(" ".concat(name).concat(" "))) {
                    value = value.concat(name).concat(" ");
                }
            }
            e.attr("class", value.trim());
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Remove a single class, multiple classes, or all classes from each element in the set of
     * matched elements.
     * </p>
     * <p>
     * If a class name is included as a parameter, then only that class will be removed from the set
     * of matched elements. If no class names are specified in the parameter, all classes will be
     * removed. More than one class may be removed at a time, separated by a space, from the set of
     * matched elements.
     * </p>
     * 
     * @param names
     * @return
     */
    public Element removeClass(String names) {
        for (Element e : this) {
            String value = " ".concat(e.attr("class")).concat(" ");

            for (String name : names.split(" ")) {
                value = value.replace(" ".concat(name).concat(" "), " ");
            }
            e.attr("class", value.trim());
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Add or remove one class from each element in the set of matched elements, depending on either
     * the class's presence or the value of the switch argument.
     * </p>
     * 
     * @param name
     * @return
     */
    public Element toggleClass(String name) {
        for (Element e : this) {
            if (e.hasClass(name)) {
                e.removeClass(name);
            } else {
                e.addClass(name);
            }
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Determine whether any of the matched elements are assigned the given class.
     * </p>
     * 
     * @param name
     * @return
     */
    public boolean hasClass(String name) {
        for (Element e : this) {
            String value = " ".concat(e.attr("class")).concat(" ");

            if (value.contains(" ".concat(name).concat(" "))) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * Reduce the set of matched elements to the first in the set.
     * </p>
     * 
     * @return
     */
    public Element first() {
        return new Element(doc, nodes.subList(0, 1));
    }

    /**
     * <p>
     * Reduce the set of matched elements to the final one in the set.
     * </p>
     * 
     * @return
     */
    public Element last() {
        int size = nodes.size();
        return new Element(doc, nodes.subList(size - 1, size));
    }

    /**
     * <p>
     * Get the descendants of each element in the current set of matched elements, filtered by a css
     * selector.
     * </p>
     * 
     * @param selector A string containing a css selector expression to match elements against.
     * @return
     */
    public Element find(String selector) {
        XPathExpression xpath = compile(selector);
        CopyOnWriteArrayList<Node> result = new CopyOnWriteArrayList();

        try {
            for (Node node : nodes) {
                NodeList list = (NodeList) xpath.evaluate(node, XPathConstants.NODESET);

                for (int i = 0; i < list.getLength(); i++) {
                    result.addIfAbsent(list.item(i));
                }
            }
            return new Element(doc, result);
        } catch (XPathExpressionException e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Return size of the current node set.
     * </p>
     * 
     * @return
     */
    public int size() {
        return nodes.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Element> iterator() {
        List<Element> elements = new ArrayList();

        for (Node node : nodes) {
            elements.add(new Element(doc, Collections.singletonList(node)));
        }
        return elements.iterator();
    }

    /**
     * <p>
     * Helper method to copy nodes.
     * </p>
     * 
     * @param xml
     * @return
     */
    private Node copy(Element xml) {
        DocumentFragment fragment = doc.createDocumentFragment();

        for (int i = 0; i < xml.nodes.size(); i++) {
            Node node = xml.nodes.get(i);

            if (doc == xml.doc) {
                fragment.appendChild(node);
            } else {
                node = doc.importNode(node, true);
                xml.nodes.set(i, node);
                fragment.appendChild(node);
            }
        }
        xml.doc = doc;
        return fragment;
    }

    /**
     * <p>
     * Parse xml fragment.
     * </p>
     * 
     * @param xml
     * @return
     */
    private static Element parse(Object xml) {
        if (xml instanceof Element) {
            return (Element) xml;
        }

        if (xml instanceof Node) {
            Node node = (Node) xml;

            if (node instanceof Document) {
                node = node.getFirstChild();
            }
            return new Element(node.getOwnerDocument(), Collections.singletonList(node));
        }

        try {
            Document doc = dom.parse(new InputSource(new StringReader("<i>" + xml + "</i>")));

            return new Element(doc, Collections.singletonList(doc.getFirstChild())).find("i:root>*");
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Compile and cache the specified selector.
     * </p>
     * 
     * @param selector A css selector.
     * @return A compiled xpath expression.
     */
    private static XPathExpression compile(String selector) {
        // check cache
        XPathExpression compiled = selectors.get(selector);

        if (compiled == null) {
            // normalize space
            selector = selector.replaceAll("\\s+", " ").replaceAll(" ([>+~\\-]) ", "$1");

            try {
                // compile actually
                compiled = xpath.compile(convert(selector));

                // cache it
                selectors.put(selector, compiled);
            } catch (XPathExpressionException e) {
                throw I.quiet(e);
            }
        }

        // API definition
        return compiled;
    }

    /**
     * <p>
     * Helper method to convert css selector to xpath.
     * </p>
     * 
     * @param selector A css selector.
     * @return A xpath.
     */
    private static String convert(String selector) {
        StringBuilder xpath = new StringBuilder();
        Matcher matcher = SELECTOR.matcher(selector);

        while (matcher.find()) {
            // =================================================
            // Combinators
            // =================================================
            String suffix = null;
            String match = matcher.group(1);

            if (match == null) {
                // no combinator
                if (matcher.start() == 0) {
                    // first selector
                    xpath.append("descendant-or-self::");
                } else {
                    break; // finish parsing
                }
            } else {
                switch (match.charAt(0)) {
                case '>': // Child combinator
                    xpath.append('/');
                    break;

                case ' ': // Descendant combinator
                    xpath.append("//");
                    break;

                case '~': // General sibling combinator
                    xpath.append("/following-sibling::");
                    break;

                case '+': // Adjacent sibling combinator
                    xpath.append("/following-sibling::*[1][self::");
                    suffix = "]";
                    break;

                case '-': // Adjacent previous sibling combinator (EXTENSION)
                    xpath.append("/preceding-sibling::*[1][self::");
                    suffix = "]";
                    break;
                }
            }

            // =================================================
            // Type (Universal) Selector
            // =================================================
            match = matcher.group(2);

            if (match == null) {
                xpath.append("*");
            } else {
                xpath.append(match);
            }

            if (suffix != null) {
                xpath.append(suffix);
            }

            // =================================================
            // ID Selector
            // =================================================
            match = matcher.group(4);

            if (match != null) {
                xpath.append("[@id='" + match + "']");
            }

            // =================================================
            // Class Selector
            // =================================================
            match = matcher.group(5);

            if (match != null && match.length() != 0) {
                for (String className : match.substring(1).split("\\.")) {
                    xpath.append("[contains(concat(' ',normalize-space(@class),' '),' " + className + " ')]");
                }
            }

            // =================================================
            // Attribute Selector
            // =================================================
            match = matcher.group(8);

            if (match != null) {
                String value = matcher.group(11);

                if (value == null) {
                    // [att]
                    //
                    // Represents an element with the att attribute, whatever the value
                    // of the attribute.
                    xpath.append("[@").append(match).append("]");
                } else {
                    String type = matcher.group(10);

                    if (type == null) {
                        // [att=val]
                        //
                        // Represents an element with the att attribute whose value
                        // is exactly "val".
                        xpath.append("[@").append(match).append("='").append(value).append("']");
                    } else {
                        switch (type.charAt(0)) {
                        case '~':
                            // [att~=val]
                            //
                            // Represents an element with the att attribute whose value is a
                            // whitespace-separated list of words, one of which is exactly
                            // "val". If "val" contains whitespace, it will never represent
                            // anything (since the words are separated by spaces). Also if "val"
                            // is the empty string, it will never represent anything.
                            xpath.append("[contains(concat(' ',@")
                                    .append(match)
                                    .append(",' '),' ")
                                    .append(value)
                                    .append(" ')]");
                            break;

                        case '*':
                            // [att*=val]
                            //
                            // Represents an element with the att attribute whose value contains
                            // at least one instance of the substring "val". If "val" is the
                            // empty string then the selector does not represent anything.
                            xpath.append("[contains(@").append(match).append(",'").append(value).append("')]");
                            break;

                        case '^':
                            // [att^=val]
                            //
                            // Represents an element with the att attribute whose value begins
                            // with the prefix "val". If "val" is the empty string then the
                            // selector does not represent anything.
                            xpath.append("[starts-with(@").append(match).append(",'").append(value).append("')]");
                            break;

                        case '$':
                            // [att$=val]
                            //
                            // Represents an element with the att attribute whose value ends
                            // with the suffix "val". If "val" is the empty string then the
                            // selector does not represent anything.
                            xpath.append("[substring(@")
                                    .append(match)
                                    .append(", string-length(@")
                                    .append(match)
                                    .append(") - string-length('")
                                    .append(value)
                                    .append("') + 1) = '")
                                    .append(value)
                                    .append("']");
                            break;

                        case '|':
                            // [att|=val]
                            //
                            // Represents an element with the att attribute, its value either
                            // being exactly "val" or beginning with "val" immediately followed
                            // by "-" (U+002D).
                            break;
                        }
                    }
                }
            }

            // =================================================
            // Structural Pseudo Classes Selector
            // =================================================
            match = matcher.group(13);

            if (match != null) {
                switch (match) {
                case "first-child":
                    xpath.append("[not(preceding-sibling::*)]");
                    break;

                case "last-child":
                    xpath.append("[not(following-sibling::*)]");
                    break;

                case "first-of-type":
                    xpath.append("[not(preceding-sibling::").append(matcher.group(2)).append(")]");
                    break;

                case "last-of-type":
                    xpath.append("[not(following-sibling::").append(matcher.group(2)).append(")]");
                    break;

                case "only-child":
                    xpath.append("[count(../*) = 1]");
                    break;

                case "only-of-type":
                    xpath.append("[count(../").append(matcher.group(2)).append(")=1]");
                    break;

                case "empty":
                    xpath.append("[not(*) and not(text())]");
                    break;

                case "nth-child":
                case "nth-last-child":
                case "nth-of-type":
                case "nth-last-of-type":
                    if (matcher.group(17) == null) {
                        // odd, even or number
                        convert(xpath, match, matcher.group(2), null, matcher.group(15));
                    } else {
                        convert(xpath, match, matcher.group(2), matcher.group(16), matcher.group(19));
                    }
                    break;

                case "not":
                case "has":
                    xpath.append('[');

                    if (match.charAt(0) == 'n') {
                        xpath.append("not");
                    }
                    xpath.append('(');

                    String sub = convert(matcher.group(15));

                    if (sub.startsWith("descendant::*[")) {
                        sub = sub.replace("descendant", "self");
                    }
                    xpath.append(sub).append(")]");
                    break;

                case "parent":
                    xpath.append("/..");
                    break;

                case "root":
                    xpath.delete(0, xpath.length()).append("/*");
                    break;

                case "contains":
                    xpath.append("[contains(text(),'").append(matcher.group(15)).append("')]");
                    break;
                }
            }
        }
        return xpath.toString();
    }

    /**
     * <p>
     * Helper method to compile nth-pusedo-selectors.
     * </p>
     * 
     * @param xpath
     * @param clazz
     * @param type
     * @param coefficient
     * @param remainder
     */
    private static void convert(StringBuilder xpath, String clazz, String type, String coefficient, String remainder) {
        String direction = "preceding";

        if (clazz.contains("last")) {
            direction = "following";
        }

        if (clazz.endsWith("child")) {
            type = "*";
        }

        if (remainder == null) {
            remainder = "0";
        } else if (remainder.equals("even")) {
            coefficient = "2";
            remainder = "0";
        } else if (remainder.equals("odd")) {
            coefficient = "2";
            remainder = "1";
        }

        xpath.append("[(count(").append(direction).append("-sibling::").append(type).append(") + 1)");

        if (coefficient != null) {
            if (coefficient.length() == 0) {
                coefficient = "1";
            }
            xpath.append(" mod ").append(coefficient);
        }
        xpath.append('=').append(remainder).append("]");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringWriter buffer;
        try {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            buffer = new StringWriter();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            for (Node node : nodes) {
                transformer.transform(new DOMSource(node), new StreamResult(buffer));
            }
        } catch (TransformerConfigurationException e) {
            throw I.quiet(e);
        } catch (IllegalArgumentException e) {
            throw I.quiet(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw I.quiet(e);
        } catch (TransformerException e) {
            throw I.quiet(e);
        }

        return buffer.toString();
    }
}
