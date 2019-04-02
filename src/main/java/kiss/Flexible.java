/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss;

import java.util.Objects;

/**
 * General purpose flexible and invokable function interface.
 */
public interface Flexible<Self> {

    /**
     * Invoke method with the specified parameters and return some value (may be null). This method
     * doesn't check type correctness, so runtime may be going to throw {@link ClassCastException}.
     * 
     * @param params A list of parameters.
     * @return A some value (may be null).
     */
    Object invoke(Object... params);

    default <F extends Flexible> F as(Class<F> type) {
        return I.make(null, Objects.requireNonNull(type), args -> invoke(I.array(args, null, null, null)));
    }
}
