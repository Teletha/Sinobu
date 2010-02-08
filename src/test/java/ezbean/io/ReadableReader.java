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
package ezbean.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * @version 2010/01/08 11:47:20
 */
public class ReadableReader extends Reader {

    /** The actual input. */
    private final Readable readable;

    /**
     * @param readable
     */
    public ReadableReader(Readable readable) {
        this.readable = readable;
    }

    /**
     * @see java.io.Reader#close()
     */
    @Override
    public void close() throws IOException {
        FileSystem.close(readable);
    }

    /**
     * @see java.io.Reader#read(char[], int, int)
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return readable.read(CharBuffer.wrap(cbuf, off, len));
    }
}