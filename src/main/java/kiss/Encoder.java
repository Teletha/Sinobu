/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss;

/**
 * <p>
 * Codec for interconversion of object and {@link String}.
 * </p>
 * <p>
 * This class provides all-purpose codec for literalization and provids the default encoder which
 * uses {@link String#valueOf(Object)}.
 * </p>
 *
 * @param <M> A model class to decode and encode.
 * @see Decoder
 * @version 2016/01/20 10:39:37
 */
public interface Encoder<M> extends Extensible {

    /**
     * <p>
     * Encode the model object to {@link String}.
     * </p>
     * 
     * @param value A model object to encode.
     * @return A encoded {@link String} representation.
     * @throws IllegalArgumentException If the given value is illegal format.
     */
    public String encode(M value);
}