/*
 * Copyright (C) 2011 Nameless Production Committee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
