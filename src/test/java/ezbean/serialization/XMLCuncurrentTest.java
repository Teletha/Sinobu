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
package ezbean.serialization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ezbean.I;
import ezbean.sample.bean.StringList;
import ezunit.CleanRoom;

/**
 * @version 2011/03/29 12:37:35
 */
public class XMLCuncurrentTest {

    /** The temporaries. */
    @Rule
    public static final CleanRoom room = new CleanRoom();

    /** The serialization file. */
    private static final Path testFile = room.locateFile("config.xml");

    /** Thread pool for this test. */
    private ExecutorService pool = Executors.newFixedThreadPool(2);

    /**
     * Initialize all resources.
     */
    @Before
    public void init() throws Exception {
        // create new thread pool
        pool = Executors.newFixedThreadPool(4);

    }

    /**
     * Release all resources.
     */
    @After
    public void release() throws Exception {
        // shutdown all pooled threads
        pool.shutdownNow();
    }

    /**
     * Test method for {@link ezbean.Configuration#read(java.io.File, java.lang.Object)}.
     */
    @Test
    public void testReadAndWrite1() throws Exception {
        StringList bean = createBigList();

        // write
        pool.execute(new Writer(bean));

        // read
        Future<StringList> future = pool.submit(new Reader());

        StringList result = future.get();

        assert result != null;
        assert result.getList() != null;
        assert 100000 == result.getList().size();
    }

    private StringList createBigList() {
        List list = new ArrayList(100000);

        for (int i = 0; i < 100000; i++) {
            list.add(i);
        }

        StringList bean = I.make(StringList.class);
        bean.setList(list);

        return bean;
    }

    /**
     * @version 2011/03/29 12:37:30
     */
    private static class Reader implements Callable<StringList> {

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public StringList call() throws Exception {
            return I.read(Files.newBufferedReader(testFile, I.getEncoding()), I.make(StringList.class));
        }
    }

    /**
     * @version 2011/03/29 12:37:27
     */
    private static class Writer implements Runnable {

        private final Object bean;

        /**
         * Create Writer instance.
         * 
         * @param bean
         */
        private Writer(Object bean) {
            this.bean = bean;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                I.write(bean, Files.newBufferedWriter(testFile, I.getEncoding()), false);
            } catch (IOException e) {
                throw I.quiet(e);
            }
        }
    }
}