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
package ezunit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;

/**
 * DOCUMENT.
 * 
 * @version 2008/06/01 14:37:08
 */
public abstract class MultiThreadTestCase {

    /** The thread pool. */
    protected ExecutorService executor;

    /**
     * Initialize all resources.
     */
    @Before
    public void init() throws Exception {
        // create new thread pool
        executor = Executors.newCachedThreadPool();

    }

    /**
     * Release all resources.
     */
    @After
    public void release() throws Exception {
        // shutdown all pooled threads
        executor.shutdownNow();
    }
}
