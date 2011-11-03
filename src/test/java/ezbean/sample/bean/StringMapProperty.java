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

import java.util.Map;

/**
 * DOCUMENT.
 * 
 * @author <a href="mailto:Teletha.T@gmail.com">Teletha Testarossa</a>
 * @version $ Id: StringMap.java,v 1.0 2007/02/18 12:39:08 Teletha Exp $
 */
public class StringMapProperty {

    private Map<String, String> map;

    /**
     * Get the map property of this {@link StringMapProperty}.
     * 
     * @return The map prperty.
     */
    public Map<String, String> getMap() {
        return map;
    }

    /**
     * Set the map property of this {@link StringMapProperty}.
     * 
     * @param map The map value to set.
     */
    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}