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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @version 2016/10/23 13:23:45
 */
public class Variable<V> {

    /** The modifier. */
    private static final Field modify;

    static {
        try {
            modify = Variable.class.getField("v");
            modify.setAccessible(true);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /** The current value. This value is not final but read-only. */
    public transient final V v;

    /** The immutability state. */
    private volatile boolean immune;

    /** The observers. */
    private volatile List<Observer> observers;

    /**
     * Hide constructor.
     */
    private Variable(V value) {
        this.v = value;
    }

    /**
     * <p>
     * Compute the current value. If it is <code>null</code>, this method returns the specified
     * default value.
     * </p>
     *
     * @param value The default value.
     * @return The current value or the specified default value.
     */
    public V get() {
        return get((V) null);
    }

    /**
     * <p>
     * Compute the current value. If it is <code>null</code>, this method returns the specified
     * default value.
     * </p>
     *
     * @param value The default value.
     * @return The current value or the specified default value.
     */
    public V get(V value) {
        return get(() -> value);
    }

    /**
     * <p>
     * Compute the current value. If it is <code>null</code>, this method returns the specified
     * default value.
     * </p>
     *
     * @param value The default value supplier.
     * @return The current value or the specified default value.
     */
    public V get(Supplier<V> value) {
        return v == null ? value == null ? null : value.get() : v;
    }

    /**
     * @param value A value to check the equality.
     * @return A result of equality.
     */
    public boolean is(V value) {
        return Objects.equals(v, value);
    }

    /**
     * @param value A value to check the equality.
     * @return A result of equality.
     */
    public boolean is(BooleanSupplier condition) {
        return condition == null ? false : condition.getAsBoolean();
    }

    /**
     * @param value A value to check the equality.
     * @return A result of equality.
     */
    public boolean is(Predicate<V> condition) {
        return condition == null ? false : condition.test(v);
    }

    /**
     * Check whether the value is absent or not.
     *
     * @return A result.
     */
    public boolean isAbsent() {
        return is(Objects::isNull);
    }

    /**
     * Check whether the value is present or not.
     *
     * @return A result.
     */
    public boolean isPresent() {
        return is(Objects::nonNull);
    }

    // /**
    // * <p>
    // * Perform the specified action if the value is present.
    // * </p>
    // *
    // * @param action An action to perform.
    // */
    // public void map(Consumer<V> action) {
    // if (v != null && action != null) {
    // action.accept(v);
    // }
    // }

    /**
     * <p>
     * Perform the specified action if the value is present.
     * </p>
     *
     * @param then An action to perform.
     * @return The computed {@link Variable}.
     */
    public <R> Variable<R> map(Function<? super V, ? extends R> then) {
        return map(then, empty());
    }

    /**
     * <p>
     * Perform the specified action if the value is present.
     * </p>
     *
     * @param action An action to perform.
     * @return The computed {@link Variable}.
     */
    public <R> Variable<R> map(Function<? super V, ? extends R> then, R or) {
        return map(then, of(or));
    }

    /**
     * <p>
     * Perform the specified action if the value is present.
     * </p>
     *
     * @param action An action to perform.
     * @return The computed {@link Variable}.
     */
    public <R> Variable<R> map(Function<? super V, ? extends R> then, Supplier<R> or) {
        return map(then, of(or));
    }

    /**
     * <p>
     * Perform the specified action if the value is present.
     * </p>
     *
     * @param action An action to perform.
     * @return The computed {@link Variable}.
     */
    public <R> Variable<R> map(Function<? super V, ? extends R> then, Variable<R> or) {
        return v == null || then == null ? or : of(then.apply(v));
    }

    /**
     * <p>
     * Perform the specified action if the value is present.
     * </p>
     *
     * @param action An action to perform.
     * @return The computed {@link Variable}.
     */
    public <R> Variable<R> flatMap(Function<V, Variable<R>> converter) {
        return v == null || converter == null ? new Variable(null) : converter.apply(v);
    }

    /**
     * <p>
     * Observe this {@link Variable}.
     * </p>
     *
     * @return
     */
    public Events<V> observe() {
        return new Events<V>(observer -> {
            if (observers == null) {
                observers = new CopyOnWriteArrayList();
            }
            observers.add(observer);

            return () -> {
                observers.remove(observer);

                if (observers.isEmpty()) {
                    observers = null;
                }
            };
        });
    }

    /**
     * <p>
     * If the value is present, return this {@link Variable}. If the value is absent, return other
     * {@link Variable}.
     * </p>
     *
     * @param other An other value.
     * @return A {@link Variable}.
     */
    public Variable<V> or(V other) {
        return v != null ? this : of(other);
    }

    /**
     * <p>
     * If the value is present, return this {@link Variable}. If the value is absent, return other
     * {@link Variable}.
     * </p>
     *
     * @param other An other value.
     * @return A {@link Variable}.
     */
    public Variable<V> or(Supplier<V> other) {
        return v != null ? this : of(other);
    }

    /**
     * <p>
     * If the value is present, return this {@link Variable}. If the value is absent, return other
     * {@link Variable}.
     * </p>
     *
     * @param other An other value.
     * @return A {@link Variable}.
     */
    public <Param> Variable<V> or(Param param, Function<Param, V> other) {
        return v != null ? this : of(other.apply(param));
    }

    /**
     * <p>
     * If the value is present, return this {@link Variable}. If the value is absent, return other
     * {@link Variable}.
     * </p>
     *
     * @param other An other value.
     * @return A {@link Variable}.
     */
    public Variable<V> or(Optional<V> other) {
        return v != null ? this : of(other);
    }

    /**
     * <p>
     * If the value is present, return this {@link Variable}. If the value is absent, return other
     * {@link Variable}.
     * </p>
     *
     * @param other An other value.
     * @return A {@link Variable}.
     */
    public Variable<V> or(Variable<V> other) {
        return v != null ? this : other != null ? other : new Variable(null);
    }

    /**
     * <p>
     * Assign the new value.
     * </p>
     *
     * @param value A value to assign.
     * @return A previous value.
     */
    public V set(V value) {
        return setIf(I.accept(), value);
    }

    /**
     * <p>
     * Assign the new value.
     * </p>
     *
     * @param value A value generator.
     * @return A previous value.
     */
    public V set(Supplier<V> value) {
        return setIf(I.accept(), value);
    }

    /**
     * <p>
     * Assign the new value.
     * </p>
     *
     * @param value A value generator.
     * @return A previous value.
     */
    public V set(UnaryOperator<V> value) {
        return setIf(I.accept(), value);
    }

    /**
     * <p>
     * Assign the new value when the specified condition is valid.
     * </p>
     *
     * @param condition A condition for value assign.
     * @param value A value to assign.
     * @return A previous value.
     */
    public V setIf(Predicate<V> condition, V value) {
        return setIf(condition, current -> value);
    }

    /**
     * <p>
     * Assign the new value when the specified condition is valid.
     * </p>
     *
     * @param condition A condition for value assign.
     * @param value A value to assign.
     * @return A previous value.
     */
    public V setIf(Predicate<V> condition, Supplier<V> value) {
        return setIf(condition, current -> value == null ? current : value.get());
    }

    /**
     * <p>
     * Assign the new value when the specified condition is valid.
     * </p>
     *
     * @param condition A condition for value assign.
     * @param value A value to assign.
     * @return A previous value.
     */
    public V setIf(Predicate<V> condition, UnaryOperator<V> value) {
        V previous = v;

        if (condition != null && value != null && condition.test(previous)) {
            try {
                modify.set(this, value.apply(previous));
            } catch (Exception e) {
                throw I.quiet(e);
            }

            if (observers != null) {
                for (Observer observer : observers) {
                    observer.accept(v);
                }
            }
        }
        return previous;
    }

    /**
     * <p>
     * Assign the new value when the specified condition is valid. Then, this {@link Variable}
     * becomes immutable.
     * </p>
     *
     * @param condition A condition for value assign.
     * @param value A value to assign.
     * @return A previous value.
     */
    public V letIf(Predicate<V> condition, UnaryOperator<V> value) {
        return v;
    }

    /**
     * <p>
     * Create {@link Variable} with the specified value.
     * </p>
     *
     * @param value An actual value, <code>null</code> will be acceptable.
     * @return A created {@link Variable}.
     */
    public static <T> Variable<T> of(T value) {
        return new Variable(value);
    }

    /**
     * <p>
     * Create {@link Variable} with the specified value.
     * </p>
     *
     * @param value An actual value, <code>null</code> will be acceptable.
     * @return A created {@link Variable}.
     */
    public static <T> Variable<T> of(Supplier<T> value) {
        return of(value.get());
    }

    /**
     * <p>
     * Create {@link Variable} with the specified value.
     * </p>
     *
     * @param value An actual value, <code>null</code> will be acceptable.
     * @return A created {@link Variable}.
     */
    public static <T> Variable<T> of(Optional<T> value) {
        return of(value.orElse(null));
    }

    /**
     * <p>
     * Create empty {@link Variable}.
     * </p>
     *
     * @return
     */
    public static <T> Variable<T> empty() {
        return new Variable(null);
    }
}
