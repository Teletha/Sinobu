/*
 * Copyright (C) 2011 Nameless Production Committee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ezbean.sample.bean;

import ezbean.sample.modifier.Final;

/**
 * @version 2011/12/09 20:53:25
 */
public final class FinalBean {

    /** The property. */
    private int property;

    /**
     * Get the property property of this {@link Final}.
     * 
     * @return The property property.
     */
    public int getProperty() {
        return property;
    }

    /**
     * Set the property property of this {@link Final}.
     * 
     * @param property The property value to set.
     */
    public void setProperty(int property) {
        this.property = property;
    }
}