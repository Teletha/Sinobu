import bee.task.Test;

/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
public class Project extends bee.api.Project {

    {
        product("com.github.teletha", "sinobu", "1.2");
        producer("Nameless Production Committee");
        describe("Sinobu is not obsolete framework but utility, which can manipulate objects as a extremely-condensed facade.");

        require("com.github.teletha", "antibug", "LATEST").atTest();
        require("io.reactivex.rxjava2", "rxjava", "LATEST").atTest();

        versionControlSystem("https://github.com/teletha/Sinobu");
    }

    {
        Test.showProlongedTest = 300;
    }
}
