/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package ezbean;

import java.util.Locale;

import ezbean.model.Codec;

/**
 * <p>
 * This is dual-purpose implementation class. One is codec for {@link Class}. The other is lifestyle
 * for {@link Locale}.
 * </p>
 * <p>
 * This class locates ezbean package to access {@link Modules}'s package-private field.
 * </p>
 * 
 * @version 2010/01/16 19:33:09
 */
class ClassCodec extends Codec<Class> implements Lifestyle<Locale> {

    /**
     * @see ezbean.model.Codec#decode(java.lang.String)
     */
    public Class decode(String value) {
        for (Module module : I.make(Modules.class).modules) {
            try {
                return module.loadClass(value);
            } catch (ClassNotFoundException e) {
                // continue
            }
        }
        throw new IllegalArgumentException(value);
    }

    /**
     * @see ezbean.model.Codec#encode(java.lang.Object)
     */
    public String encode(Class value) {
        return value.getName();
    }

    /**
     * @see ezbean.Lifestyle#resolve()
     */
    public Locale resolve() {
        return Locale.getDefault();
    }
}
