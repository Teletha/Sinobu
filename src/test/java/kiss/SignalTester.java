/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

import antibug.Chronus;
import kiss.signal.Subject;

/**
 * @version 2017/04/07 16:52:37
 */
public class SignalTester {

    /** The complete state for {@link #emit(Object...)}. */
    protected static final Object Complete = new Object();

    /** The error state for {@link #emit(Object...)}. */
    protected static final Class Error = Error.class;

    private static final Chronus clock = new Chronus(I.class);

    /** default multiplicity */
    private static final int multiplicity = 2;

    /** Shorthand for {@link TimeUnit#MILLISECONDS}. */
    protected static final TimeUnit ms = TimeUnit.MILLISECONDS;

    /** The alias of 'this' for DSL. */
    protected final SignalTester Type = this;

    /** The alias of 'Function.identity()' for DSL. */
    protected final Function Same = Function.identity();

    /** READ ONLY : DON'T MODIFY in test case */
    protected Log result = null;

    /** READ ONLY : DON'T MODIFY in test case */
    protected Log log1 = null;

    /** READ ONLY : DON'T MODIFY in test case */
    protected Log log2 = null;

    /** READ ONLY : DON'T MODIFY in test case */
    protected Log log3 = null;

    /** READ ONLY : DON'T MODIFY in test case */
    protected Log log4 = null;

    /** READ ONLY : DON'T MODIFY in test case */
    protected Log log5 = null;

    /** READ ONLY : DON'T MODIFY in test case */
    protected Disposable disposer = null;

    protected Publisher other = new PublisherImplementation();

    protected Subject<Boolean, Boolean> condition = new Subject();

    /** READ ONLY : DON'T MODIFY in test case */
    private final List<Observer> observers = new CopyOnWriteArrayList();

    /** READ ONLY : DON'T MODIFY in test case */
    private final List<Await> awaits = new CopyOnWriteArrayList();

    /**
     * Create generic error {@link Function}.
     * 
     * @return
     */
    protected final <P, R> Function<P, R> errorFunction() {
        return e -> {
            throw new Error();
        };
    };

    /**
     * Create generic error {@link Iterable}.
     * 
     * @return
     */
    protected final <T> Iterable<T> errorIterable() {
        return () -> {
            throw new Error();
        };
    }

    protected final Signal completeAfter(int time, TimeUnit unit) {
        Await await = new Await();
        awaits.add(await);

        return new Signal<>((observer, disposer) -> {
            I.schedule(time, unit, false, () -> {
                observer.complete();
                await.completed = true;
            });
            return disposer;
        });
    }

    /**
     * <p>
     * Helper method to emit the specified values to all monitored {@link Observer}.
     * </p>
     */
    protected final Log emit(Object... values) {
        for (Observer observer : observers) {
            for (Object value : values) {
                if (value == Complete) {
                    observer.complete();
                } else if (value instanceof Class && Throwable.class.isAssignableFrom((Class) value)) {
                    observer.error(I.make((Class<Throwable>) value));
                } else {
                    observer.accept(value);
                }
            }
        }
        return result;
    }

    protected final void dispose() {
        disposer.dispose();
    }

    protected final Log await() {
        clock.await();

        return result;
    }

    protected final Log await(int ms) {
        clock.freeze(ms);

        return result;
    }

    /**
     * Shorthand method of {@link I#list(Object...)}.
     * 
     * @param values
     * @return
     */
    protected <T> List<T> list(T... values) {
        return I.list(values);
    }

    /**
     * Shorthand method of {@link Stream#of}.
     * 
     * @param values
     * @return
     */
    protected <T> Stream<T> stream(T... values) {
        return Stream.of(values);
    }

    /**
     * Shorthand method of {@link Collections#enumeration(java.util.Collection)}.
     * 
     * @param values
     * @return
     */
    protected <T> Enumeration<T> enume(T... values) {
        return Collections.enumeration(I.list(values));
    }

    /**
     * Shorthand method of {@link I#signal(Object...)}
     * 
     * @param values
     * @return
     */
    protected <T> Signal<T> signal(T... values) {
        return I.signal(values);
    }

    /**
     * Shorthand method of {@link I#signalInfinite(Object, long, TimeUnit)}
     * 
     * @param values
     * @return
     */
    protected <T> Signal<T> signal(T value, int time, TimeUnit unit) {
        return I.signalInfinite(value, time, unit);
    }

    /**
     * Shorthand method of {@link I#signal(Iterable)}
     * 
     * @param values
     * @return
     */
    protected <T> Signal<T> signal(Iterable values) {
        return I.signal(values);
    }

    /**
     * Shorthand method of {@link I#signal(Enumeration)}
     * 
     * @param values
     * @return
     */
    protected <T> Signal<T> signal(Enumeration values) {
        return I.signal(values);
    }

    /**
     * Shorthand method of {@link I#signal(BaseStream)}
     * 
     * @param values
     * @return
     */
    protected <T, S extends BaseStream<T, S>> Signal<T> signal(S values) {
        return I.signal(values);
    }

    /**
     * <p>
     * Monitor signal to test.
     * </p>
     * 
     * @param signal
     */
    protected void monitor(Supplier<Signal> signal) {
        monitor(multiplicity, signal);
    }

    /**
     * <p>
     * Monitor signal to test.
     * </p>
     * 
     * @param signal
     */
    protected void monitor(int multiplicity, Supplier<Signal> signal) {
        LogSet[] sets = new LogSet[multiplicity];

        for (int i = 0; i < multiplicity; i++) {
            sets[i] = new LogSet();
            log1 = sets[i].log1;
            log2 = sets[i].log2;
            log3 = sets[i].log3;
            log4 = sets[i].log4;
            log5 = sets[i].log5;
            result = sets[i].result;

            sets[i].disposer = signal.get().to(result);
        }

        // await all awaitable signal
        for (Await awaiter : awaits) {
            awaiter.await();
        }

        log1 = I.bundle(stream(sets).map(e -> e.log1).collect(toList()));
        log2 = I.bundle(stream(sets).map(e -> e.log2).collect(toList()));
        log3 = I.bundle(stream(sets).map(e -> e.log3).collect(toList()));
        log4 = I.bundle(stream(sets).map(e -> e.log4).collect(toList()));
        log5 = I.bundle(stream(sets).map(e -> e.log5).collect(toList()));
        result = I.bundle(stream(sets).map(e -> e.result).collect(toList()));
        disposer = I.bundle(Disposable.class, stream(sets).map(e -> e.disposer).collect(toList()));
    }

    /**
     * <p>
     * Monitor signal to test.
     * </p>
     * 
     * @param signal
     */
    protected <T> void monitor(Function<Signal<T>, Signal<T>> signal) {
        monitor(multiplicity, signal);
    }

    /**
     * <p>
     * Monitor signal to test.
     * </p>
     * 
     * @param signal
     */
    protected <T> void monitor(Class<T> type, Function<Signal<T>, Signal<T>> signal) {
        monitor(multiplicity, signal);
    }

    /**
     * <p>
     * Monitor signal to test.
     * </p>
     * 
     * @param builder
     */
    protected <T> void monitor(int multiplicity, Function<Signal<T>, Signal<T>> builder) {
        LogSet[] sets = new LogSet[multiplicity];

        for (int i = 0; i < multiplicity; i++) {
            sets[i] = new LogSet();
            log1 = sets[i].log1;
            log2 = sets[i].log2;
            log3 = sets[i].log3;
            log4 = sets[i].log4;
            log5 = sets[i].log5;
            result = sets[i].result;

            Signal signal = new Signal<>((observer, disposer) -> {
                observers.add(observer);
                return disposer.add(() -> observers.remove(observer));
            });

            sets[i].disposer = builder.apply(signal).to(result);
        }

        // await all awaitable signal
        for (Await awaiter : awaits) {
            awaiter.await();
        }

        log1 = I.bundle(stream(sets).map(e -> e.log1).collect(toList()));
        log2 = I.bundle(stream(sets).map(e -> e.log2).collect(toList()));
        log3 = I.bundle(stream(sets).map(e -> e.log3).collect(toList()));
        log4 = I.bundle(stream(sets).map(e -> e.log4).collect(toList()));
        log5 = I.bundle(stream(sets).map(e -> e.log5).collect(toList()));
        result = I.bundle(stream(sets).map(e -> e.result).collect(toList()));
        disposer = I.bundle(Disposable.class, stream(sets).map(e -> e.disposer).collect(toList()));
    }

    /**
     * @version 2017/04/04 12:59:48
     */
    protected static interface Log<T> extends Observer<T> {
        /**
         * <p>
         * Cehck this subscription is completed or not.
         * </p>
         * 
         * @return A result.
         */
        boolean isCompleted();

        /**
         * <p>
         * Cehck this subscription is completed or not.
         * </p>
         * 
         * @return A result.
         */
        boolean isNotCompleted();

        /**
         * <p>
         * Cehck this subscription has error or not.
         * </p>
         * 
         * @return A result.
         */
        boolean isError();

        /**
         * <p>
         * Cehck this subscription has error or not.
         * </p>
         * 
         * @return A result.
         */
        boolean isNotError();

        /**
         * Validate the result values.
         * 
         * @param expected
         * @return
         */
        boolean value(Object... expected);
    }

    /**
     * @version 2017/04/02 1:14:02
     */
    private static class Logger implements Log {

        private List values = new ArrayList();

        private boolean completed;

        private Throwable error;

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(Object value) {
            values.add(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void complete() {
            completed = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void error(Throwable error) {
            this.error = error;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCompleted() {
            return completed == true && error == null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isNotCompleted() {
            return completed == false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isError() {
            assert error != null;
            assert completed == false;
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isNotError() {
            assert error == null;
            assert completed == false;
            return true;
        }

        /**
         * Validate the result values and clear them from log.
         * 
         * @param expected
         * @return
         */
        @Override
        public boolean value(Object... expected) {
            assert values.size() == expected.length;

            for (int i = 0; i < expected.length; i++) {
                assert Objects.equals(values.get(i), expected[i]);
            }
            values.clear();
            return true;
        }
    }

    /**
     * @version 2017/04/04 12:52:06
     */
    private class LogSet {

        Log result = new Logger();

        Log log1 = new Logger();

        Log log2 = new Logger();

        Log log3 = new Logger();

        Log log4 = new Logger();

        Log log5 = new Logger();

        Disposable disposer;
    }

    /**
     * @version 2017/04/06 12:38:00
     */
    private class Await {

        boolean completed;

        /**
         * Await completed event.
         */
        private void await() {
            int count = 0;

            while (completed == false) {
                try {
                    Thread.sleep(10);

                    if (100 < count) {
                        throw new IllegalThreadStateException("Test must execute within 1 sec.");
                    }
                } catch (InterruptedException e) {
                    throw I.quiet(e);
                }
            }
        }
    }

    /**
     * @version 2017/04/16 1:45:49
     */
    public interface Publisher {

        Log emit(Object... values);

        Signal signal();

        boolean isCompleted();

        boolean isNotCompleted();
    }

    /**
     * @version 2017/04/16 1:45:47
     */
    private class PublisherImplementation implements Publisher {

        private List<Observer> observers = new CopyOnWriteArrayList();

        /**
         * {@inheritDoc}
         */
        @Override
        public Log emit(Object... values) {
            for (Observer observer : observers) {
                for (Object value : values) {
                    if (value == Complete) {
                        observer.complete();
                    } else if (value instanceof Class && Throwable.class.isAssignableFrom((Class) value)) {
                        observer.error(I.make((Class<Throwable>) value));
                    } else {
                        observer.accept(value);
                    }
                }
            }
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Signal signal() {
            Signal signal = new Signal<>((observer, disposer) -> {
                observers.add(observer);
                return disposer.add(() -> {
                    observers.remove(observer);
                });
            });
            return signal;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isNotCompleted() {
            return !observers.isEmpty();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCompleted() {
            return observers.isEmpty();
        }
    }
}