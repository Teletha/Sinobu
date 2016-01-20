/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss;

import java.lang.reflect.Method;
import java.nio.file.Path;

import antibug.ReusableRule;

/**
 * <p>
 * Tweek Sinobu environment temporary for test.
 * </p>
 * 
 * @version 2011/03/30 18:07:26
 */
public class SinobuSetting extends ReusableRule {

    /** The temporary working directory. */
    public final Path working;

    /** The original working directory. */
    private final Path workingOriginal;

    public SinobuSetting(Path working) {
        this.working = working;
        this.workingOriginal = I.$working;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void before(Method method) throws Exception {
        I.$working = working;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void after(Method method) {
        I.$working = workingOriginal;
    }
}
