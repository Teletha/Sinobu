/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss;

/**
 * <p>
 * This lifestyle guarantees that only one instance of the specific class exists in Sinobu.
 * </p>
 * 
 * @see Prototype
 * @see ThreadSpecific
 * @see Preference
 * @version 2011/11/04 0:11:41
 */
public class Singleton<M> extends Prototype<M> {

    /** The singleton instance. */
    protected final M instance;

    /**
     * Create Singleton instance.
     * 
     * @param modelClass
     */
    protected Singleton(Class<M> modelClass) {
        super(modelClass);

        instance = super.resolve();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public M resolve() {
        return instance;
    }
}