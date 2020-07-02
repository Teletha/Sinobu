/*
 * Copyright (C) 2020 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package kiss;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import kiss.model.Model;
import kiss.model.Property;

public class JSON {

    /** The reusable selector separator pattern. */
    private static final Pattern P = Pattern.compile("\\.");

    /** The root object. */
    private Object root;

    /**
     * Create empty JSON object.
     */
    public JSON() {
        this(new HashMap());
    }

    /**
     * Hide constructor.
     * 
     * @param root A root json object.
     */
    JSON(Object root) {
        this.root = root;
    }

    /**
     * Get the direct child value as your type with the specified key. Unknown key and object key
     * will return null.
     * 
     * @param key A key for value to find.
     * @param type A value type to find.
     * @return An associated value.
     */
    public <T> T get(String key, Class<T> type) {
        return root instanceof Map ? I.transform(((Map) root).get(key), type) : null;
    }

    /**
     * Set the direct child value with the specified key.
     * 
     * @param key A key.
     * @param value A value.
     * @return Chainable API.
     */
    public JSON set(String key, Object value) {
        if (root instanceof Map) {
            ((Map) root).put(key, value);
        }
        return this;
    }

    /**
     * Find values by the specified property path.
     * 
     * @param path A property path.
     * @return A traversed {@link JSON} stream.
     */
    public Signal<JSON> find(String path) {
        Signal<Object> current = I.signal(root);

        for (String name : P.split(path)) {
            current = current.flatMap(v -> {
                if (v instanceof Map == false) {
                    return Signal.never();
                } else if (name.equals("*")) {
                    return I.signal(((Map) v).values());
                } else if (name.equals("^")) {
                    return I.signal(((Map) v).values()).reverse();
                } else {
                    return I.signal(((Map) v).get(name));
                }
            });
        }
        return current.map(JSON::new);
    }

    /**
     * Find values by the specified property path.
     * 
     * @param path A property path.
     * @param type A property type you want.
     * @return A traversed {@link JSON} stream.
     */
    public <T> Signal<T> find(String path, Class<T> type) {
        return find(path).map(v -> v.to(type));
    }

    /**
     * <p>
     * Data mapping to the specified model.
     * </p>
     * 
     * @param type A model type.
     * @return A created model.
     */
    public <M> M to(Class<M> type) {
        if (JSON.class == type) {
            return (M) this;
        }
        Model<M> model = Model.of(type);
        return model.attribute ? I.transform(root, type) : to(model, I.make(type), root);
    }

    /**
     * <p>
     * Data mapping to the specified model.
     * </p>
     * 
     * @param value A model.
     * @return A specified model.
     */
    public <M> M to(M value) {
        return to(Model.of(value), value, root);
    }

    /**
     * <p>
     * Helper method to traverse json structure using Java Object {@link Model}.
     * </p>
     *
     * @param <M> A current model type.
     * @param model A java object model.
     * @param java A java value.
     * @param js A javascript value.
     * @return A restored java object.
     */
    private <M> M to(Model<M> model, M java, Object js) {
        if (js instanceof Map) {
            for (Entry<String, Object> e : ((Map<String, Object>) js).entrySet()) {
                Property p = model.property(e.getKey());

                if (p != null && !p.isTransient) {
                    Object value = e.getValue();

                    // convert value
                    if (p.isAttribute()) {
                        value = I.transform(value, p.model.type);
                    } else if (value != null) {
                        Object nest = model.get(java, p);
                        String impl = (String) ((Map) value).get("#");
                        Model m = impl == null ? p.model : Model.of(I.type(impl));
                        value = to(m, nest == null ? I.make(m.type) : nest, value);
                    }

                    // assign value
                    model.set(java, p, value);
                }
            }
        }

        // API definition
        return java;
    }

    // ===========================================================
    // Parser API
    // ===========================================================
    /** The input source. */
    private Reader reader;

    /** The input buffer. */
    private char[] buffer;

    /** The index of input buffer. */
    private int index;

    /** The limit of input buffer. */
    private int fill;

    /** The current character data. */
    private int current;

    /** The capturing text. */
    private StringBuilder capture;

    /** The capture index in input buffer. */
    private int captureStart;

    /**
     * Initialize parser.
     * 
     * @param reader
     * @throws IOException
     */
    JSON(Reader reader) throws IOException {
        this.reader = reader;
        this.buffer = new char[1024];
        this.captureStart = -1;

        read();
        space();
        root = value();
    }

    /**
     * <p>
     * Read value.
     * </p>
     * 
     * @throws IOException
     */
    private Object value() throws IOException {
        switch (current) {
        // keyword
        case 'n':
            return keyword(null);
        case 't':
            return keyword(Boolean.TRUE);
        case 'f':
            return keyword(Boolean.FALSE);

        // string
        case '"':
            return string();

        // array
        case '[':
            Map array = new LinkedHashMap();
            read();
            space();
            if (read(']')) {
                return array;
            }

            int count = 0;
            do {
                space();
                array.put(String.valueOf(count++), value());
                space();
            } while (read(','));
            token(']');
            return array;

        // object
        case '{':
            Map object = new HashMap();
            read();
            space();
            if (read('}')) {
                return object;
            }
            do {
                space();
                String name = string();
                space();
                token(':');
                space();
                object.put(name, value());
                space();
            } while (read(','));
            token('}');
            return object;

        // number
        case '-':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            startCapture();
            read('-');
            if (current == '0') {
                read();
            } else {
                digit();
            }

            // fraction
            if (read('.')) {
                digit();
            }

            // exponent
            if (read('e') || read('E')) {
                if (!read('+')) {
                    read('-');
                }
                digit();
            }
            return endCapture();

        // invalid token
        default:
            return expected("value");
        }
    }

    /**
     * <p>
     * Read the sequence of white spaces
     * </p>
     * 
     * @throws IOException
     */
    private void space() throws IOException {
        while (current == ' ' || current == '\t' || current == '\n' || current == '\r') {
            read();
        }
    }

    /**
     * <p>
     * Read the sequence of digit.
     * </p>
     * 
     * @throws IOException
     */
    private void digit() throws IOException {
        int count = 0;

        while ('0' <= current && current <= '9') {
            read();
            count++;
        }

        if (count == 0) {
            expected("digit");
        }
    }

    /**
     * <p>
     * Read the sequence of keyword.
     * </p>
     * 
     * @param keyword A target value.
     * @return A target value.
     * @throws IOException
     */
    private Object keyword(Object keyword) throws IOException {
        read();

        String value = String.valueOf(keyword);

        for (int i = 1; i < value.length(); i++) {
            token(value.charAt(i));
        }
        return keyword;
    }

    /**
     * <p>
     * Read the sequence of String.
     * </p>
     * 
     * @return A parsed string.
     * @throws IOException
     */
    private String string() throws IOException {
        token('"');
        startCapture();
        while (current != '"') {
            if (current == '\\') {
                pauseCapture();
                // escape
                read();
                switch (current) {
                case '"':
                case '/':
                case '\\':
                    capture.append((char) current);
                    break;
                case 'b':
                    capture.append('\b');
                    break;
                case 'f':
                    capture.append('\f');
                    break;
                case 'n':
                    capture.append('\n');
                    break;
                case 'r':
                    capture.append('\r');
                    break;
                case 't':
                    capture.append('\t');
                    break;
                case 'u':
                    char[] chars = new char[4];
                    for (int i = 0; i < 4; i++) {
                        read();
                        chars[i] = (char) current;
                    }
                    capture.append((char) Integer.parseInt(new String(chars), 16));
                    break;
                default:
                    expected("escape sequence");
                }
                read();
                startCapture();
            } else if (current < 0x20) {
                expected("string character");
            } else {
                read();
            }
        }
        String string = endCapture();
        read();
        return string;
    }

    /**
     * <p>
     * Read the next character.
     * </p>
     * 
     * @throws IOException
     */
    private void read() throws IOException {
        if (index == fill) {
            if (captureStart != -1) {
                capture.append(buffer, captureStart, fill - captureStart);
                captureStart = 0;
            }
            fill = reader.read(buffer, 0, buffer.length);
            index = 0;
            if (fill == -1) {
                current = -1;
                return;
            }
        }
        current = buffer[index++];
    }

    /**
     * <p>
     * Read the specified character.
     * </p>
     * 
     * @param c The character to be red.
     * @return A result.
     * @throws IOException
     */
    private boolean read(char c) throws IOException {
        if (current == c) {
            read();
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>
     * Read the specified character surely.
     * </p>
     * 
     * @param c The character to be red.
     * @return A result.
     * @throws IOException
     */
    private void token(char c) throws IOException {
        if (current == c) {
            read();
        } else {
            expected(c);
        }
    }

    /**
     * Start text capturing.
     */
    private void startCapture() {
        if (capture == null) {
            capture = new StringBuilder();
        }
        captureStart = index - 1;
    }

    /**
     * Pause text capturing.
     */
    private void pauseCapture() {
        int end = current == -1 ? index : index - 1;
        capture.append(buffer, captureStart, end - captureStart);
        captureStart = -1;
    }

    /**
     * Stop text capturing.
     */
    private String endCapture() {
        int end = current == -1 ? index : index - 1;
        String captured;
        if (capture.length() > 0) {
            capture.append(buffer, captureStart, end - captureStart);
            captured = capture.toString();
            capture.setLength(0);
        } else {
            captured = new String(buffer, captureStart, end - captureStart);
        }
        captureStart = -1;
        return captured;
    }

    /**
     * <p>
     * Throw parsing error.
     * </p>
     * 
     * @param expected A reason.
     * @return This method NEVER return value.
     */
    private Object expected(Object expected) {
        throw new IllegalStateException("Expected : ".concat(String.valueOf(expected)));
    }

    // ===========================================================
    // Writer API
    // ===========================================================
    /** The charcter sequence for output as JSON. */
    private Appendable out;

    /**
     * JSON serializer for Java object graph.
     */
    JSON(Appendable out) {
        this.out = out;
    }

    /**
     * JSON serializer for Java object graph. This serializer rejects cyclic node within ancestor
     * nodes, but same object in sibling nodes will be acceptable.
     * 
     * @param model
     * @param property
     * @param value
     */
    void write(Model model, Property property, Object value) {
        if (!property.isTransient && property.name != null) {
            try {
                // non-first properties requires separator
                if (index++ != 0) out.append(',');

                // all properties need the properly indents
                if (0 < current) {
                    out.append("\r\n").append("\t".repeat(current)); // indent

                    // property key (List node doesn't need key)
                    if (model.type != List.class) {
                        write(property.name, String.class);
                        out.append(model.type == Bundle.class ? ":\n\t\t" : ": ");
                    }
                }

                // property value
                if (property.isAttribute()) {
                    write(I.transform(value, String.class), property.model.type);
                } else if (value == null) {
                    out.append("null");
                } else {
                    if (64 < current) {
                        throw new ClassCircularityError();
                    }

                    JSON walker = new JSON(out);
                    walker.current = current + 1;
                    out.append(property.model.type == List.class ? '[' : '{');
                    Model<Object> m = property.model;
                    if (Modifier.isAbstract(m.type.getModifiers()) && m.getClass() == Model.class) {
                        m = Model.of(value);
                        out.append("\r\n").append("\t".repeat(current + 1)).append("\"#\": \"").append(m.type.getName()).append("\",");
                    }
                    m.walk(value, walker::write);
                    if (walker.index != 0) out.append("\r\n").append("\t".repeat(current)); // indent
                    out.append(property.model.type == List.class ? ']' : '}');
                }
            } catch (IOException e) {
                throw I.quiet(e);
            }
        }
    }

    /**
     * <p>
     * Write JSON literal with quote.
     * </p>
     * 
     * @param value A value.
     * @param type A value type.
     * @throws IOException
     */
    private void write(String value, Class type) throws IOException {
        if (value == null) {
            out.append("null");
        } else {
            boolean primitive = type.isPrimitive() && type != char.class;

            if (!primitive) out.append('"');

            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);

                switch (c) {
                case '"':
                    out.append("\\\"");
                    break;

                case '\\':
                    out.append("\\\\");
                    break;

                case '\b':
                    out.append("\\b");
                    break;

                case '\f':
                    out.append("\\f");
                    break;

                case '\n':
                    out.append("\\n");
                    break;

                case '\r':
                    out.append("\\r");
                    break;

                case '\t':
                    out.append("\\t");
                    break;

                default:
                    out.append(c);
                }
            }
            if (!primitive) out.append('"');
        }
    }
}