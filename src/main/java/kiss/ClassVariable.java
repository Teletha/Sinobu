/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package kiss;

/**
 * <p>
 * {@link ClassVariable} provides the GC-aware variable which is associated with the specific
 * {@link Class}.
 * </p>
 * 
 * @version 2014/07/24 9:11:42
 */
public class ClassVariable<T> extends ClassValue {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object computeValue(Class type) {
        return new Subscriber();
    }

    /**
     * <p>
     * Set the value for the given {@link Class}. If some value has been associated already and this
     * variable is variance, the given value will override the current value.
     * </p>
     * 
     * @param type A class type as key.
     * @param value An associated value.
     * @return The current value.
     */
    public synchronized T set(Class type, T value) {
        Subscriber<T> holder = (Subscriber<T>) super.get(type);

        if (value != null && holder.index == 0) {
            holder.object = value;
        }
        return holder.object;
    }

    /**
     * <p>
     * Set the value for the given {@link Class}. If some value has been associated already and this
     * variable is variance, the given value will override the current value.
     * </p>
     * 
     * @param type A class type as key.
     * @param value An associated value.
     * @return The current value.
     */
    public synchronized T let(Class type, T value) {
        Subscriber<T> holder = (Subscriber<T>) super.get(type);

        if (value != null && holder.index++ == 0) {
            holder.object = value;
        }
        return holder.object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(Class type) {
        return ((Subscriber<T>) super.get(type)).object;
    }
}
