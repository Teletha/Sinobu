/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.category.instance;

import kiss.category.Monoid;

/**
 * @version 2016/03/29 14:31:27
 */
class Strings implements Monoid<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String empty() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String append(String one, String other) {
        return one + other;
    }
}