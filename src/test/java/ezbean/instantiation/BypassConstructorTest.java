/*
 * Copyright (C) 2010 Nameless Production Committee.
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
package ezbean.instantiation;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import sun.reflect.ReflectionFactory;
import ezbean.I;
import ezbean.module.ModuleLoader;

/**
 * @version 2010/02/09 18:01:58
 */
public class BypassConstructorTest {

    private static int call = 0;

    @Before
    public void reset() {
        call = 0;
    }

    @Test
    public void byReflectionFactory() throws Exception {
        // no bypass
        Child child = I.make(Child.class);
        assertTrue(child instanceof Child);
        assertEquals(2, call);

        // bypass
        child = (Child) ReflectionFactory.getReflectionFactory()
                .newConstructorForSerialization(Child.class, Object.class.getConstructor())
                .newInstance();
        assertTrue(child instanceof Child);
        assertEquals(2, call); // Cool!!!
    }

    @Test
    public void byObjectStreamClass() throws Exception {
        // no bypass
        Child child = I.make(Child.class);
        assertTrue(child instanceof Child);
        assertEquals(2, call);

        // bypass
        Method method = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[] {});
        method.setAccessible(true);

        child = (Child) method.invoke(ObjectStreamClass.lookup(Child.class));
        assertTrue(child instanceof Child);
        assertEquals(3, call); // Umm...
    }

    @Test
    public void byMockObjectInputStream() throws Exception {
        // no bypass
        Child child = I.make(Child.class);
        assertTrue(child instanceof Child);
        assertEquals(2, call);

        // bypass
        child = (Child) new Mock(Child.class).readObject();
        assertTrue(child instanceof Child);
        assertEquals(3, call); // Umm...
    }

    /**
     * @version 2010/02/09 18:16:25
     */
    private static class Mock extends ObjectInputStream {

        /** The heading data for serializaed object. */
        private static final byte[] head = {-84, -19, 0, 5, 115, 114};

        /** The tailing data for serializaed object. */
        private static final byte[] tail = {2, 0, 0, 120, 112, 115, 113, 0, 126, 0, 0};

        /**
         * @param clazz
         */
        private Mock(Class clazz) throws IOException {
            super(build(clazz));
        }

        /**
         * @param clazz
         * @return
         * @throws IOException
         */
        private static InputStream build(Class clazz) throws IOException {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            DataOutputStream writer = new DataOutputStream(output);
            writer.writeUTF(clazz.getName());
            writer.writeLong(ObjectStreamClass.lookup(clazz).getSerialVersionUID());

            byte[] data = output.toByteArray();
            byte[] bytes = new byte[16 + data.length];
            System.arraycopy(head, 0, bytes, 0, 6);
            System.arraycopy(data, 0, bytes, 6, data.length);
            System.arraycopy(tail, 0, bytes, 6 + data.length, 6);

            // API definition
            return new ByteArrayInputStream(bytes);
        }

        /**
         * @see java.io.ObjectInputStream#resolveClass(java.io.ObjectStreamClass)
         */
        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            return ModuleLoader.getModuleLoader(null).loadClass(desc.getName());
        }
    }

    /**
     * @version 2010/02/09 18:03:40
     */
    protected static class Parent {

        private String value;

        /**
         * 
         */
        public Parent() {
            call++;
        }

        /**
         * Get the value property of this {@link BypassConstructorTest.Parent}.
         * 
         * @return The value property.
         */
        public String getValue() {
            return value;
        }

        /**
         * Set the value property of this {@link BypassConstructorTest.Parent}.
         * 
         * @param value The value value to set.
         */
        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * @version 2010/02/09 18:13:21
     */
    protected static class Child extends Parent implements Serializable {

        private static final long serialVersionUID = 8601055221631424018L;

        /**
         * 
         */
        public Child() {
            call++;
        }
    }
}
