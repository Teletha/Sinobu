/*
 * Copyright (C) 2010 Nameless Production Committee.
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
package ezbean;

import ezbean.model.Model;
import ezbean.model.Property;
import ezbean.model.PropertyWalker;

/**
 * <p>
 * This is dual-purpose implementation class. One is a state recorder for configuration and the
 * other a {@link PropertyWalker} implementation for bean transformation.
 * </p>
 * 
 * @version 2008/07/30 15:27:18
 */
final class ModelState implements PropertyWalker {

    /** The current model. */
    Model model;

    /** The curret object. */
    Object object;

    Property property;

    int i = 0;

    /**
     * Create State instance.
     * 
     * @param object A actual object.
     * @param model A model of the specified object.
     */
    ModelState(Object object, Model model) {
        this.object = object;
        this.model = model;
    }

    /**
     * @see ezbean.model.PropertyWalker#walk(ezbean.model.Model, ezbean.model.Property,
     *      java.lang.Object)
     */
    public void walk(Model model, Property property, Object node) {
        Property dest = this.model.getProperty(property.name);

        // never check null because PropertyWalker traverses existing properties
        this.model.set(object, dest, I.transform(node, dest.model.type));
    }

}
