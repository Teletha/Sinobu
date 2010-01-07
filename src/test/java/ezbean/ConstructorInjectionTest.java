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

import static org.junit.Assert.*;



import org.junit.Test;

import ezbean.I;
import ezbean.Manageable;
import ezbean.Singleton;
import ezbean.sample.dependency.CircularA;
import ezbean.sample.dependency.CircularB;


/**
 * DOCUMENT.
 * 
 * @version 2008/06/10 10:19:49
 */
public class ConstructorInjectionTest {

    /**
     * Test constructor injection.
     */
    @Test
    public void testConstructorInjection() {
        ConstructorInjection test = I.make(ConstructorInjection.class);
        assertNotNull(test);
        assertNotNull(test.injected);
    }

    /**
     * Test singleton injection.
     */
    @Test
    public void testSingletonInjetion() {
        ConstructorSingletonInjection test1 = I.make(ConstructorSingletonInjection.class);
        assertNotNull(test1);
        assertNotNull(test1.injected);

        ConstructorSingletonInjection test2 = I.make(ConstructorSingletonInjection.class);
        assertNotNull(test2);
        assertNotNull(test2.injected);

        assertNotSame(test1, test2);
        assertEquals(test1.injected, test2.injected);
    }

    /**
     * Test too many constructors.
     */
    @Test
    public void testTooManyConstructors() {
        TooManyConstructors test = I.make(TooManyConstructors.class);
        assertNotNull(test);
        assertNull(test.injected);
    }

    /**
     * DOCUMENT.
     * 
     * @author <a href="mailto:Teletha.T@gmail.com">Teletha Testarossa</a>
     * @version $ Id: ConstructorInjection.java,v 1.0 2007/01/23 17:51:31 Teletha Exp $
     */
    public static class ConstructorInjection {

        /** The dependency, */
        private Injected injected;

        /**
         * Create ConstructorInjection instance.
         * 
         * @param injected
         */
        public ConstructorInjection(Injected injected) {
            this.injected = injected;
        }
    }

    /**
     * DOCUMENT.
     * 
     * @author <a href="mailto:Teletha.T@gmail.com">Teletha Testarossa</a>
     * @version $ Id: ConstructorSingletonInjection.java,v 1.0 2007/01/23 17:51:31 Teletha Exp $
     */
    public static class ConstructorSingletonInjection {

        /** The dependency, */
        private SingletonInjected injected;

        /**
         * Create ConstructorSingletonInjection instance.
         * 
         * @param injected
         */
        public ConstructorSingletonInjection(SingletonInjected injected) {
            this.injected = injected;
        }
    }

    /**
     * DOCUMENT.
     * 
     * @author <a href="mailto:Teletha.T@gmail.com">Teletha Testarossa</a>
     * @version $ Id: TooManyConstructors.java,v 1.0 2007/01/23 19:29:51 Teletha Exp $
     */
    public static class TooManyConstructors {

        /** The dependency, */
        private Injected injected;

        /**
         * Create TooManyConstructors instance.
         * 
         * @param invalid
         */
        public TooManyConstructors() {
        }

        /**
         * Create TooManyConstructors instance.
         * 
         * @param injected
         */
        public TooManyConstructors(Injected injected) {
            this.injected = injected;
        }
    }

    /**
     * DOCUMENT.
     * 
     * @author <a href="mailto:Teletha.T@gmail.com">Teletha Testarossa</a>
     * @version $ Id: Injected.java,v 1.0 2007/01/23 19:23:40 Teletha Exp $
     */
    public static class Injected {
    }

    /**
     * DOCUMENT.
     * 
     * @author <a href="mailto:Teletha.T@gmail.com">Teletha Testarossa</a>
     * @version $ Id: SingletonInjected.java,v 1.0 2007/01/23 19:23:40 Teletha Exp $
     */
    @Manageable(lifestyle = Singleton.class)
    public static class SingletonInjected {
    }

    /**
     * Circular dependency.
     */
    @Test(expected = ClassCircularityError.class)
    public void testCircularDependency01() {
        I.make(CircularA.class);
    }

    /**
     * Circular dependency.
     */
    @Test(expected = ClassCircularityError.class)
    public void testCircularDependency02() {
        I.make(CircularB.class);
    }
}
