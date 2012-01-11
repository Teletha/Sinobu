/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package ezbean.sample.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * @version 2010/01/09 13:57:51
 */
public class IncompatibleKeyMap {

    private Map<Serializable, Class> incompatible;

    /**
     * Get the incompatible property of this {@link IncompatibleKeyMap}.
     * 
     * @return The incompatible property.
     */
    public Map<Serializable, Class> getIncompatible() {
        return incompatible;
    }

    /**
     * Set the incompatible property of this {@link IncompatibleKeyMap}.
     * 
     * @param incompatible The incompatible value to set.
     */
    public void setIncompatible(Map<Serializable, Class> incompatible) {
        this.incompatible = incompatible;
    }
}
