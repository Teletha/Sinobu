/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.lang;

/**
 * @version 2017/02/07 11:25:21
 */
public interface Declarable<N> {

    /**
     * <p>
     * Declare the definition.
     * </p>
     * 
     * @param context A context.
     */
    void declare(N context);

    /**
     * <p>
     * Conflate this {@link Declarable} with the specified {@link Declarable}.
     * </p>
     * 
     * @param other An other {@link Declarable} to conflate.
     * @return A conflated {@link Declarable}.
     */
    default Declarable<N> and(Declarable<N> other) {
        return context -> {
            declare(context);
            other.declare(context);
        };
    }
}
