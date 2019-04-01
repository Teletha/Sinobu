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

/**
 * General purpose flexible and invokable function interface.
 */
public interface Flexible {

    /**
     * General purpose to invoke method with the specified parameters and return some value (may be
     * null).
     * 
     * @param params A list of parameters.
     * @return A some value (may be null).
     */
    Object invoke(Object... params);
}
