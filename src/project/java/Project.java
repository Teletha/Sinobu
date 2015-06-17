/*
 * Copyright (C) 2015 Sinobu Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
public class Project extends bee.api.Project {

    {
        product("npc", "Sinobu", "0.9.4");
        describe("Sinobu is not obsolete framework but utility, which can manipulate objects as a extremely-condensed facade.");

        require("npc", "antibug", "0.3").atTest();
        require("junit", "junit", "4.10").atTest();

        unrequire("org.hamcrest", "hamcrest-core");
    }
}
