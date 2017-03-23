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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @version 2016/10/23 13:23:45
 */
public class Variable<V> implements Consumer<V>, Supplier<V> {

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

    /** The immutability. */
    private final AtomicBoolean fix = new AtomicBoolean();

    /** The observers. */
    private volatile List<Observer> observers;

    /**
     * Hide constructor.
     */
    private Variable(V value) {
        this.v = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(V value) {
        set(v);
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
    @Override
    public V get() {
        return v;
    }

    /**
     * <p>
     * Test whether the current value is equal to the specified value or not.
     * </p>
     * 
     * @param value A value to check the equality.
     * @return A result of equality.
     */
    public final boolean is(V value) {
        return Objects.equals(v, value);
    }

    /**
     * <p>
     * Test whether the current value is NOT equal to the specified value or not.
     * </p>
     * 
     * @param value A value to check the equality.
     * @return A result of equality.
     */
    public final boolean isNot(V value) {
        return !is(value);
    }

    /**
     * <p>
     * Test whether the current value is equal to the specified value or not.
     * </p>
     * 
     * @param value A value to check the equality.
     * @return A result of equality.
     */
    public final boolean is(BooleanSupplier condition) {
        return condition == null ? false : condition.getAsBoolean();
    }

    /**
     * <p>
     * Test whether the current value is NOT equal to the specified value or not.
     * </p>
     * 
     * @param value A value to check the equality.
     * @return A result of equality.
     */
    public final boolean isNot(BooleanSupplier condition) {
        return !is(condition);
    }

    /**
     * <p>
     * Test whether the current value fulfills the specified condition or not.
     * </p>
     * 
     * @param value A value to check the equality.
     * @return A result of equality.
     */
    public final boolean is(Predicate<V> condition) {
        return condition == null ? false : condition.test(v);
    }

    /**
     * <p>
     * Test whether the current value does NOT fulfill the specified condition or not.
     * </p>
     * 
     * @param value A value to check the equality.
     * @return A result of equality.
     */
    public final boolean isNot(Predicate<V> condition) {
        return !is(condition);
    }

    /**
     * Emulate if statement.
     * 
     * @param value A conditional value.
     * @param valid A valid process.
     * @return Chainable API.
     */
    public final Variable<V> is(V value, Consumer<V> valid) {
        return is(value, valid, null);
    }

    /**
     * Emulate if statement.
     * 
     * @param value A conditional value.
     * @param valid A valid process.
     * @return Chainable API.
     */
    public final Variable<V> isNot(V value, Consumer<V> valid) {
        return isNot(value, valid, null);
    }

    /**
     * Emulate if-else statement.
     * 
     * @param condition A conditional.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return Chainable API.
     */
    public final Variable<V> is(V condition, Consumer<V> valid, Consumer<V> invalid) {
        return effect(is(condition), valid, invalid);
    }

    /**
     * Emulate if-else statement.
     * 
     * @param condition A conditional.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return Chainable API.
     */
    public final Variable<V> isNot(V condition, Consumer<V> valid, Consumer<V> invalid) {
        return effect(isNot(condition), valid, invalid);
    }

    /**
     * Emulate if statement.
     * 
     * @param condition A conditional.
     * @param valid A valid process.
     * @return Chainable API.
     */
    public final Variable<V> is(BooleanSupplier condition, Consumer<V> valid) {
        return is(condition, valid, null);
    }

    /**
     * Emulate if statement.
     * 
     * @param condition A conditional.
     * @param valid A valid process.
     * @return Chainable API.
     */
    public final Variable<V> isNot(BooleanSupplier condition, Consumer<V> valid) {
        return isNot(condition, valid, null);
    }

    /**
     * Emulate if-else statement.
     * 
     * @param condition A conditional.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return Chainable API.
     */
    public final Variable<V> is(BooleanSupplier condition, Consumer<V> valid, Consumer<V> invalid) {
        return effect(is(condition), valid, invalid);
    }

    /**
     * Emulate if-else statement.
     * 
     * @param condition A conditional.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return Chainable API.
     */
    public final Variable<V> isNot(BooleanSupplier condition, Consumer<V> valid, Consumer<V> invalid) {
        return effect(isNot(condition), valid, invalid);
    }

    /**
     * Emulate if statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return Chainable API.
     */
    public final Variable<V> is(Predicate<V> condition, Consumer<V> valid) {
        return is(condition, valid, null);
    }

    /**
     * Emulate if statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return Chainable API.
     */
    public final Variable<V> isNot(Predicate<V> condition, Consumer<V> valid) {
        return isNot(condition, valid, null);
    }

    /**
     * Emulate if-else statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return Chainable API.
     */
    public final Variable<V> is(Predicate<V> condition, Consumer<V> valid, Consumer<V> invalid) {
        return effect(is(condition), valid, invalid);
    }

    /**
     * Emulate if-else statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return Chainable API.
     */
    public final Variable<V> isNot(Predicate<V> condition, Consumer<V> valid, Consumer<V> invalid) {
        return effect(isNot(condition), valid, invalid);
    }

    /**
     * Emulate if-else statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return Chainable API.
     */
    private Variable<V> effect(boolean condition, Consumer<V> valid, Consumer<V> invalid) {
        if (condition) {
            if (valid != null) valid.accept(v);
        } else {
            if (invalid != null) invalid.accept(v);
        }
        return this;
    }

    /**
     * Emulate if and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> is(V condition, Function<V, R> valid) {
        return is(condition, valid, null);
    }

    /**
     * Emulate if and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> isNot(V condition, Function<V, R> valid) {
        return isNot(condition, valid, null);
    }

    /**
     * Emulate if-else and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> is(V condition, Function<V, R> valid, Function<V, R> invalid) {
        return map(is(condition), valid, invalid);
    }

    /**
     * Emulate if-else and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> isNot(V condition, Function<V, R> valid, Function<V, R> invalid) {
        return map(isNot(condition), valid, invalid);
    }

    /**
     * Emulate if and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> is(BooleanSupplier condition, Function<V, R> valid) {
        return is(condition, valid, null);
    }

    /**
     * Emulate if and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> isNot(BooleanSupplier condition, Function<V, R> valid) {
        return isNot(condition, valid, null);
    }

    /**
     * Emulate if-else and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> is(BooleanSupplier condition, Function<V, R> valid, Function<V, R> invalid) {
        return map(is(condition), valid, invalid);
    }

    /**
     * Emulate if-else and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> isNot(BooleanSupplier condition, Function<V, R> valid, Function<V, R> invalid) {
        return map(isNot(condition), valid, invalid);
    }

    /**
     * Emulate if and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> is(Predicate<V> condition, Function<V, R> valid) {
        return is(condition, valid, null);
    }

    /**
     * Emulate if and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> isNot(Predicate<V> condition, Function<V, R> valid) {
        return isNot(condition, valid, null);
    }

    /**
     * Emulate if-else and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> is(Predicate<V> condition, Function<V, R> valid, Function<V, R> invalid) {
        return map(is(condition), valid, invalid);
    }

    /**
     * Emulate if-else and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> isNot(Predicate<V> condition, Function<V, R> valid, Function<V, R> invalid) {
        return map(isNot(condition), valid, invalid);
    }

    /**
     * Emulate if-else and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return The returned value or empty.
     */
    private <R> Variable<R> map(boolean condition, Function<V, R> valid, Function<V, R> invalid) {
        return map(v -> {
            if (condition) {
                return valid == null ? null : valid.apply(v);
            } else {
                return invalid == null ? null : invalid.apply(v);
            }
        });
    }

    /**
     * Check whether the value is absent or not.
     *
     * @return A result.
     */
    public final boolean isAbsent() {
        return is(Objects::isNull);
    }

    /**
     * Emulate if and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return The returned value or empty.
     */
    public final Variable<V> isAbsent(Consumer<V> valid) {
        return isAbsent(valid, null);
    }

    /**
     * Emulate if-else and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return The returned value or empty.
     */
    public final Variable<V> isAbsent(Consumer<V> valid, Consumer<V> invalid) {
        return effect(isAbsent(), valid, invalid);
    }

    /**
     * Emulate if and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> isAbsent(Function<V, R> valid) {
        return isAbsent(valid, null);
    }

    /**
     * Emulate if-else and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> isAbsent(Function<V, R> valid, Function<V, R> invalid) {
        return map(isAbsent(), valid, invalid);
    }

    /**
     * Check whether the value is present or not.
     *
     * @return A result.
     */
    public final boolean isPresent() {
        return is(Objects::nonNull);
    }

    /**
     * Emulate if and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return The returned value or empty.
     */
    public final Variable<V> isPresent(Consumer<V> valid) {
        return isPresent(valid, null);
    }

    /**
     * Emulate if-else and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return The returned value or empty.
     */
    public final Variable<V> isPresent(Consumer<V> valid, Consumer<V> invalid) {
        return effect(isPresent(), valid, invalid);
    }

    /**
     * Emulate if and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> isPresent(Function<V, R> valid) {
        return isPresent(valid, null);
    }

    /**
     * Emulate if-else and return statement.
     * 
     * @param condition A condition.
     * @param valid A valid process.
     * @param invalid A invalid process.
     * @return The returned value or empty.
     */
    public final <R> Variable<R> isPresent(Function<V, R> valid, Function<V, R> invalid) {
        return map(isPresent(), valid, invalid);
    }

    /**
     * <p>
     * Perform the specified action if the value is present.
     * </p>
     *
     * @param then An action to perform.
     * @return The computed {@link Variable}.
     */
    public <R> Variable<R> map(Function<? super V, ? extends R> then) {
        if (v != null && then != null) {
            try {
                return of(then.apply(v));
            } catch (Throwable e) {
                // ignore
            }
        }
        return empty();
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
        return new Events<>((observer, disposer) -> {
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
        return v != null ? this : other != null ? other : empty();
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
     * @param value A value to assign.
     * @return A previous value.
     */
    public V set(Optional<V> value) {
        return setIf(I.accept(), value);
    }

    /**
     * <p>
     * Assign the new value.
     * </p>
     *
     * @param value A value to assign.
     * @return A previous value.
     */
    public V set(Variable<V> value) {
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
        return setIf(condition, of(value));
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
    public V setIf(Predicate<V> condition, Optional<V> value) {
        return setIf(condition, of(value));
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
    public V setIf(Predicate<V> condition, Variable<V> value) {
        return assign(condition, value, false);
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
        return setIf(condition, of(value));
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
        return setIf(condition, value == null ? null : value.apply(v));
    }

    /**
     * <p>
     * Assign the new immutable value.
     * </p>
     *
     * @param value A value to assign.
     * @return A previous value.
     */
    public V let(V value) {
        return letIf(I.accept(), value);
    }

    /**
     * <p>
     * Assign the new immutable value.
     * </p>
     *
     * @param value A value to assign.
     * @return A previous value.
     */
    public V let(Optional<V> value) {
        return letIf(I.accept(), value);
    }

    /**
     * <p>
     * Assign the new immutable value.
     * </p>
     *
     * @param value A value to assign.
     * @return A previous value.
     */
    public V let(Variable<V> value) {
        return letIf(I.accept(), value);
    }

    /**
     * <p>
     * Assign the new immutable value.
     * </p>
     *
     * @param value A value generator.
     * @return A previous value.
     */
    public V let(Supplier<V> value) {
        return letIf(I.accept(), value);
    }

    /**
     * <p>
     * Assign the new immutable value.
     * </p>
     *
     * @param value A value generator.
     * @return A previous value.
     */
    public V let(UnaryOperator<V> value) {
        return letIf(I.accept(), value);
    }

    /**
     * <p>
     * Assign the new immutable value when the specified condition is valid.
     * </p>
     *
     * @param condition A condition for value assign.
     * @param value A value to assign.
     * @return A previous value.
     */
    public V letIf(Predicate<V> condition, V value) {
        return letIf(condition, of(value));
    }

    /**
     * <p>
     * Assign the new immutable value when the specified condition is valid.
     * </p>
     *
     * @param condition A condition for value assign.
     * @param value A value to assign.
     * @return A previous value.
     */
    public V letIf(Predicate<V> condition, Optional<V> value) {
        return letIf(condition, of(value));
    }

    /**
     * <p>
     * Assign the new immutable value when the specified condition is valid.
     * </p>
     *
     * @param condition A condition for value assign.
     * @param value A value to assign.
     * @return A previous value.
     */
    public V letIf(Predicate<V> condition, Variable<V> value) {
        return assign(condition, value, true);
    }

    /**
     * <p>
     * Assign the new immutable value when the specified condition is valid.
     * </p>
     *
     * @param condition A condition for value assign.
     * @param value A value to assign.
     * @return A previous value.
     */
    public V letIf(Predicate<V> condition, Supplier<V> value) {
        return letIf(condition, of(value));
    }

    /**
     * <p>
     * Assign the new immutable value when the specified condition is valid.
     * </p>
     *
     * @param condition A condition for value assign.
     * @param value A value to assign.
     * @return A previous value.
     */
    public V letIf(Predicate<V> condition, UnaryOperator<V> value) {
        return letIf(condition, value == null ? null : value.apply(v));
    }

    /**
     * <p>
     * Assign the new value if we can.
     * </p>
     * 
     * @param condition A condition for value assign.
     * @param value A value to assign.
     * @param let A state of let or set.
     * @return A previous value.
     */
    private V assign(Predicate<V> condition, Variable<V> value, boolean let) {
        V prev = v;

        if (fix.get() == false) {
            if (is(condition)) {
                if (fix.compareAndSet(false, let)) {
                    try {
                        modify.set(this, value == null ? null : value.v);
                    } catch (Exception e) {
                        throw I.quiet(e);
                    }

                    if (observers != null) {
                        for (Observer observer : observers) {
                            observer.accept(v);
                        }
                    }
                }
            }
        }
        return prev;
    }

    /**
     * <p>
     * Require the specified condition.
     * </p>
     * 
     * @param condition
     * @return
     */
    public Variable<V> require(Predicate<V> condition) {
        return condition == null || condition.test(v) ? this : empty();
    }

    /**
     * <p>
     * Execute the specified action if the value is present.
     * </p>
     * 
     * @param action A user action.
     */
    public void to(Consumer<V> action) {
        if (v != null && action != null) {
            action.accept(v);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable == false) {
            return false;
        }
        return ((Variable) obj).is(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.valueOf(v);
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
        return of(value == null ? null : value.get());
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
        return of(value == null ? null : value.orElse(null));
    }

    /**
     * <p>
     * Create empty {@link Variable}.
     * </p>
     *
     * @return A new empty {@link Variable}.
     */
    public static <T> Variable<T> empty() {
        return new Variable(null);
    }
}
