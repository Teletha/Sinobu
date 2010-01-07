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

import java.io.File;

/**
 * DOCUMENT.
 * 
 * @version 2008/07/24 2:34:33
 */
public class EzbeanTestConfig extends I {

    /**
     * Create EzbeanTestConfig instance.
     */
    public EzbeanTestConfig() {
        workingDirectory = new File(workingDirectory, "target");
    }
}
