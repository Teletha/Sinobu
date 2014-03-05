/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @version 2014/01/14 11:04:04
 */
public class Observable<V> {

    /** For reuse. */
    public static final Observable NEVER = new Observable(observer -> {
        return new Agent();
    });

    /** For reuse. */
    private static final Predicate<Boolean> IdenticalPredicate = value -> {
        return value;
    };

    /** The subscriber. */
    private Function<Observer<? super V>, Disposable> subscriber;

    /** The unsubscriber. */
    private Disposable unsubscriber;

    /** The common value holder. */
    private AtomicReference<V> ref;

    /** The common counter. */
    private AtomicInteger counter;

    /** The common flag. */
    private AtomicBoolean flag;

    /** The common set. */
    private Set<V> set;

    /** The common deque. */
    private Queue<V> queue;

    /**
     * <p>
     * Create {@link Observable} with the specified subscriber {@link Function} which will be
     * invoked whenever you calls {@link #subscribe(Observer)} related methods.
     * </p>
     * 
     * @param subscriber A subscriber {@link Function}.
     * @see #subscribe(Observer)
     * @see #subscribe(Consumer)
     * @see #subscribe(Consumer, Consumer)
     * @see #subscribe(Consumer, Consumer, Runnable)
     */
    public Observable(Function<Observer<? super V>, Disposable> subscriber) {
        this.subscriber = subscriber;
    }

    /**
     * <p>
     * Receive values from this {@link Observable}.
     * </p>
     * 
     * @param next A delegator method of {@link Observer#onNext(Object)}.
     * @return Calling {@link Disposable#dispose()} will dispose this subscription.
     */
    public final Disposable subscribe(Consumer<? super V> next) {
        return subscribe(next, null);
    }

    /**
     * <p>
     * An {@link Observer} must call an Observable's {@code subscribe} method in order to receive
     * items and notifications from the Observable.
     * 
     * @param next A delegator method of {@link Observer#onNext(Object)}.
     * @param error A delegator method of {@link Observer#onError(Throwable)}.
     * @return Calling {@link Disposable#dispose()} will dispose this subscription.
     */
    public final Disposable subscribe(Consumer<? super V> next, Consumer<Throwable> error) {
        return subscribe(next, error, null);
    }

    /**
     * <p>
     * Receive values from this {@link Observable}.
     * </p>
     * 
     * @param next A delegator method of {@link Observer#onNext(Object)}.
     * @param error A delegator method of {@link Observer#onError(Throwable)}.
     * @param complete A delegator method of {@link Observer#onCompleted()}.
     * @return Calling {@link Disposable#dispose()} will dispose this subscription.
     */
    public final Disposable subscribe(Consumer<? super V> next, Consumer<Throwable> error, Runnable complete) {
        Agent agent = new Agent();
        agent.next = next;
        agent.error = error;
        agent.complete = complete;

        return subscribe(agent);
    }

    /**
     * <p>
     * Receive values from this {@link Observable}.
     * </p>
     * 
     * @param observer A value observer of this {@link Observable}.
     * @return Calling {@link Disposable#dispose()} will dispose this subscription.
     */
    public final Disposable subscribe(Observer<? super V> observer) {
        return unsubscriber = subscriber.apply(observer);
    }

    /**
     * <p>
     * Filters the values of an {@link Observable} sequence based on the specified type.
     * </p>
     * 
     * @param type The type of result. <code>null</code> throws {@link NullPointerException}.
     * @return Chainable API.
     * @throws NullPointerException If the type is <code>null</code>.
     */
    public final <R> Observable<R> as(Class<R> type) {
        Objects.nonNull(type);

        return (Observable<R>) filter(value -> {
            return type.isInstance(value);
        });
    }

    /**
     * <p>
     * Indicates each value of an {@link Observable} sequence into consecutive non-overlapping
     * buffers which are produced based on value count information.
     * </p>
     * 
     * @param size A length of each buffer.
     * @return Chainable API.
     */
    public final Observable<V[]> buffer(int size) {
        return buffer(size, size);
    }

    /**
     * <p>
     * Indicates each values of an {@link Observable} sequence into zero or more buffers which are
     * produced based on value count information.
     * </p>
     * 
     * @param size A length of each buffer. Zero or negative number are treated exactly the same way
     *            as 1.
     * @param interval A number of values to skip between creation of consecutive buffers. Zero or
     *            negative number are treated exactly the same way as 1.
     * @return Chainable API.
     */
    public final Observable<V[]> buffer(int size, int interval) {
        int creationSize = 0 < size ? size : 1;
        int creationInterval = 0 < interval ? interval : 1;

        return new Observable<V[]>(observer -> {
            Deque<V> buffer = new ArrayDeque();
            AtomicInteger timing = new AtomicInteger();

            return subscribe(value -> {
                buffer.offer(value);

                boolean validTiming = timing.incrementAndGet() == creationInterval;
                boolean validSize = buffer.size() == creationSize;

                if (validTiming && validSize) {
                    observer.onNext((V[]) buffer.toArray());
                }

                if (validTiming) {
                    timing.set(0);
                }

                if (validSize) {
                    buffer.pollFirst();
                }
            });
        });
    }

    /**
     * <p>
     * Drops values that are followed by newer values before a timeout. The timer resets on each
     * value emission.
     * </p>
     * 
     * @param time A time value. Zero or negative number will ignore this instruction.
     * @param unit A time unit. <code>null</code> will ignore this instruction.
     * @return Chainable API.
     */
    public final Observable<V> debounce(long time, TimeUnit unit) {
        // ignore invalid parameters
        if (time <= 0 || unit == null) {
            return this;
        }

        AtomicReference<Future> latest = new AtomicReference();

        return on((observer, value) -> {
            Future future = latest.get();

            if (future != null) {
                future.cancel(true);
            }

            Runnable task = () -> {
                latest.set(null);
                observer.onNext(value);
            };
            Future future2 = I.schedule(time, unit, task);
            System.out.println(future2);
            latest.set(future2);
        });
    }

    /**
     * <p>
     * Indicates the {@link Observable} sequence by due time with the specified source and time.
     * </p>
     * 
     * @param time The absolute time used to shift the {@link Observable} sequence. Zero or negative
     *            number will ignore this instruction.
     * @param unit A unit of time for the specified time. <code>null</code> will ignore this
     *            instruction.
     * @return Chainable API.
     */
    public final Observable<V> delay(long time, TimeUnit unit) {
        // ignore invalid parameters
        if (time <= 0 || unit == null) {
            return this;
        }

        queue = new ConcurrentLinkedQueue();

        return on((observer, value) -> {
            System.out.println("   add " + value + " on observable queue");
            queue.add(value);

            I.schedule(time, unit, () -> {
                V v = queue.poll();
                System.out.println("   retrive " + value + " on observable queue");
                observer.onNext(v);
            });
        });
    }

    /**
     * <p>
     * Returns an {@link Observable} consisting of the distinct values (according to
     * {@link Object#equals(Object)}) of this stream.
     * </p>
     * 
     * @return Chainable API.
     */
    public final Observable<V> diff() {
        return new Observable<V>(observer -> {
            ref = new AtomicReference();

            return subscribe(value -> {
                V prev = ref.getAndSet(value);

                if (!Objects.equals(prev, value)) {
                    observer.onNext(value);
                }
            });
        });
    }

    /**
     * <p>
     * Returns an {@link Observable} consisting of the distinct values (according to
     * {@link Object#equals(Object)}) of this stream.
     * </p>
     * 
     * @return Chainable API.
     */
    public final Observable<V> distinct() {
        return new Observable<V>(observer -> {
            set = new HashSet();

            return subscribe(value -> {
                if (set.add(value)) {
                    observer.onNext(value);
                }
            });
        });
    }

    /**
     * <p>
     * Returns an {@link Observable} consisting of the values of this {@link Observable} that match
     * the given predicate.
     * </p>
     * 
     * @param predicate A function that evaluates the values emitted by the source
     *            {@link Observable}, returning {@code true} if they pass the filter.
     *            <code>null</code> will ignore this instruction.
     * @return Chainable API.
     */
    public final Observable<V> filter(Predicate<? super V> predicate) {
        // ignore invalid parameters
        if (predicate == null) {
            return this;
        }

        return on((observer, value) -> {
            if (predicate.test(value)) {
                observer.onNext(value);
            }
        });
    }

    /**
     * <p>
     * Returns an {@link Observable} that applies the given constant to each item emitted by an
     * {@link Observable} and emits the result.
     * </p>
     * 
     * @param constant A constant to apply to each value emitted by this {@link Observable}.
     * @return Chainable API.
     */
    public final <R> Observable<R> map(R constant) {
        return new Observable<R>(observer -> {
            return subscribe(value -> {
                observer.onNext(constant);
            });
        });
    }

    /**
     * <p>
     * Returns an {@link Observable} that applies the given function to each value emitted by an
     * {@link Observable} and emits the result.
     * </p>
     * 
     * @param converter A converter function to apply to each value emitted by this
     *            {@link Observable}. <code>null</code> will ignore this instruction.
     * @return Chainable API.
     */
    public final <R> Observable<R> map(Function<? super V, R> converter) {
        // ignore invalid parameters
        if (converter == null) {
            return (Observable<R>) this;
        }

        return new Observable<R>(observer -> {
            return subscribe(value -> {
                observer.onNext(converter.apply(value));
            });
        });
    }

    /**
     * <p>
     * Flattens a sequence of {@link Observable} emitted by an {@link Observable} into one
     * {@link Observable}, without any transformation.
     * </p>
     * 
     * @param other A target {@link Observable} to merge. <code>null</code> will be ignroed.
     * @return Chainable API.
     */
    public final Observable<V> merge(Observable<? extends V> other) {
        // ignore invalid parameters
        if (other == null) {
            return this;
        }

        return new Observable<V>(observer -> {
            return subscribe(observer).and(other.subscribe(observer));
        });
    }

    /**
     * <p>
     * Invokes an action for each value in the {@link Observable} sequence.
     * </p>
     * 
     * @param next An action to invoke for each value in the {@link Observable} sequence.
     * @return Chainable API.
     */
    public final Observable<V> on(BiConsumer<Observer<? super V>, V> next) {
        // ignore invalid parameters
        if (next == null) {
            return this;
        }

        return new Observable<V>(observer -> {
            Agent<V> agent = new Agent();
            agent.observer = observer;
            agent.next = value -> {
                next.accept(observer, value);
            };
            return subscribe(agent);
        });
    }

    /**
     * <p>
     * Generates an {@link Observable} sequence that repeats the given value infinitely.
     * </p>
     * 
     * @return Chainable API.
     */
    public final Observable<V> repeat() {
        return new Observable<V>(observer -> {
            Agent agent = new Agent();
            agent.observer = observer;
            agent.complete = () -> {
                observer.onCompleted();
                subscribe(agent);
            };
            return subscribe(agent);
        });
    }

    /**
     * <p>
     * Generates an {@link Observable} sequence that repeats the given value finitely.
     * </p>
     * 
     * @param count A number of repeat. Zero or negative number will ignore this instruction.
     * @return Chainable API.
     */
    public final Observable<V> repeat(int count) {
        // ignore invalid parameter
        if (count < 1) {
            return this;
        }

        AtomicInteger repeat = new AtomicInteger(count);

        return new Observable<V>(observer -> {
            Agent agent = new Agent();
            agent.observer = observer;
            agent.complete = () -> {
                if (repeat.decrementAndGet() == 0) {
                    unsubscriber.dispose();
                } else {
                    unsubscriber = unsubscriber.and(subscribe(agent));
                }
            };
            return subscribe(agent);
        });
    }

    /**
     * <p>
     * Bypasses a specified number of values in an {@link Observable} sequence and then returns the
     * remaining values.
     * </p>
     * 
     * @param count A number of values to skip. Zero or negative number will ignore this
     *            instruction.
     * @return Chainable API.
     */
    public final Observable<V> skip(int count) {
        // ignore invalid parameter
        if (count <= 0) {
            return this;
        }

        return new Observable<V>(observer -> {
            counter = new AtomicInteger();

            return subscribe(value -> {
                if (count < counter.incrementAndGet()) {
                    observer.onNext(value);
                }
            });
        });
    }

    /**
     * <p>
     * Returns the values from the source {@link Observable} sequence only after the other
     * {@link Observable} sequence produces a value.
     * </p>
     * 
     * @param predicate An {@link Observable} sequence that triggers propagation of values of the
     *            source sequence. <code>null</code> will ignore this instruction.
     * @return Chainable API.
     */
    public final Observable<V> skipUntil(Observable predicate) {
        // ignore invalid parameter
        if (predicate == null) {
            return this;
        }

        return new Observable<V>(observer -> {
            flag = new AtomicBoolean();

            return subscribe(value -> {
                if (flag.get()) {
                    observer.onNext(value);
                }
            }).and(predicate.subscribe(value -> {
                flag.set(true);
            }));
        });
    }

    /**
     * <p>
     * Returns the values from the source {@link Observable} sequence only after the other
     * {@link Observable} sequence produces a value.
     * </p>
     * 
     * @param predicate An {@link Observable} sequence that triggers propagation of values of the
     *            source sequence. <code>null</code> will ignore this instruction.
     * @return Chainable API.
     */
    public final <T> Observable<V> skipUntil(Predicate<V> predicate) {
        // ignore invalid parameter
        if (predicate == null) {
            return this;
        }

        return new Observable<V>(observer -> {
            flag = new AtomicBoolean();

            return subscribe(value -> {
                if (flag.get()) {
                    observer.onNext(value);
                } else if (predicate.test(value)) {
                    flag.set(true);
                    observer.onNext(value);
                }
            });
        });
    }

    /**
     * <p>
     * Returns a specified number of contiguous values from the start of an {@link Observable}
     * sequence.
     * </p>
     * 
     * @param count A number of values to emit. Zero or negative number will ignore this
     *            instruction.
     * @return Chainable API.
     */
    public final Observable<V> take(int count) {
        // ignore invalid parameter
        if (count <= 0) {
            return this;
        }

        return new Observable<V>(observer -> {
            counter = new AtomicInteger(count);

            return subscribe(value -> {
                long current = counter.decrementAndGet();

                if (0 <= current) {
                    observer.onNext(value);

                    if (0 == current) {
                        observer.onCompleted();
                        unsubscriber.dispose();
                    }
                }
            });
        });
    }

    /**
     * <p>
     * Returns the values from the source {@link Observable} sequence until the other
     * {@link Observable} sequence produces a value.
     * </p>
     * 
     * @param predicate An {@link Observable} sequence that terminates propagation of values of the
     *            source sequence. <code>null</code> will ignore this instruction.
     * @return Chainable API.
     */
    public final Observable<V> takeUntil(Observable predicate) {
        // ignore invalid parameter
        if (predicate == null) {
            return this;
        }

        return new Observable<V>(observer -> {
            return unsubscriber = subscribe(observer).and(predicate.subscribe(value -> {
                observer.onCompleted();
                unsubscriber.dispose();
            }));
        });
    }

    /**
     * <p>
     * Returns the values from the source {@link Observable} sequence until the other
     * {@link Observable} sequence produces a value.
     * </p>
     * 
     * @param predicate An {@link Observable} sequence that terminates propagation of values of the
     *            source sequence. <code>null</code> will ignore this instruction.
     * @return Chainable API.
     */
    public final <T> Observable<V> takeUntil(Predicate<V> predicate) {
        // ignore invalid parameter
        if (predicate == null) {
            return this;
        }

        return on((observer, value) -> {
            if (predicate.test(value)) {
                observer.onNext(value);
                observer.onCompleted();
                unsubscriber.dispose();
            } else {
                observer.onNext(value);
            }
        });
    }

    /**
     * <p>
     * Throttles by skipping values until "skipDuration" passes and then emits the next received
     * value.
     * </p>
     * <p>
     * Ignores the values from an {@link Observable} sequence which are followed by another value
     * before due time with the specified source and time.
     * </p>
     * 
     * @param time Time to wait before sending another item after emitting the last item. Zero or
     *            negative number will ignore this instruction.
     * @param unit A unit of time for the specified timeout. <code>null</code> will ignore this
     *            instruction.
     * @return Chainable API.
     */
    public final Observable<V> throttle(long time, TimeUnit unit) {
        // ignore invalid parameters
        if (time <= 0 || unit == null) {
            return this;
        }

        AtomicLong latest = new AtomicLong();
        long delay = unit.toMillis(time);

        return filter(value -> {
            long now = System.currentTimeMillis();
            return latest.getAndSet(now) + delay <= now;
        });
    }

    /**
     * <p>
     * Create an {@link Observable} that emits true if all specified observables emit true as latest
     * event.
     * </p>
     * 
     * @param observables A list of target {@link Observable} to test.
     * @return Chainable API.
     */
    public static Observable<Boolean> all(Observable<Boolean>... observables) {
        return all(IdenticalPredicate, observables);
    }

    /**
     * <p>
     * Create an {@link Observable} that emits true if all specified observables emit true as latest
     * event.
     * </p>
     * 
     * @param predicate A test function.
     * @param observables A list of target {@link Observable} to test.
     * @return Chainable API.
     */
    public static <V> Observable<Boolean> all(Predicate<V> predicate, Observable<V>... observables) {
        return condition(values -> {
            for (boolean value : values) {
                if (value) {
                    return false;
                }
            }
            return true;
        }, predicate, observables);
    }

    /**
     * <p>
     * Create an {@link Observable} that emits true if any specified observable emits true as latest
     * event.
     * </p>
     * 
     * @param observables A list of target {@link Observable} to test.
     * @return Chainable API.
     */
    public static Observable<Boolean> any(Observable<Boolean>... observables) {
        return any(IdenticalPredicate, observables);
    }

    /**
     * <p>
     * Create an {@link Observable} that emits true if any specified observable emits true as latest
     * event.
     * </p>
     * 
     * @param predicate A test function.
     * @param observables A list of target {@link Observable} to test.
     * @return Chainable API.
     */
    public static <V> Observable<Boolean> any(Predicate<V> predicate, Observable<V>... observables) {
        return condition(values -> {
            for (boolean value : values) {
                if (!value) {
                    return true;
                }
            }
            return false;
        }, predicate, observables);
    }

    /**
     * <p>
     * Create an {@link Observable} that emits true if all specified observables emit false as
     * latest event.
     * </p>
     * 
     * @param observables A list of target {@link Observable} to test.
     * @return Chainable API.
     */
    public static Observable<Boolean> none(Observable<Boolean>... observables) {
        return none(IdenticalPredicate, observables);
    }

    /**
     * <p>
     * Create an {@link Observable} that emits true if all specified observables emit false as
     * latest event.
     * </p>
     * 
     * @param predicate A test function.
     * @param observables A list of target {@link Observable} to test.
     * @return Chainable API.
     */
    public static <V> Observable<Boolean> none(Predicate<V> predicate, Observable<V>... observables) {
        return condition(values -> {
            for (boolean value : values) {
                if (!value) {
                    return false;
                }
            }
            return true;
        }, predicate, observables);
    }

    /**
     * <p>
     * Helper method to merge the test result of each {@link Observable}.
     * </p>
     * 
     * @param condition A test function for result.
     * @param predicate A test function for each {@link Observable}.
     * @param observables A list of target {@link Observable} to test.
     * @return Chainable API.
     */
    private static <V> Observable<Boolean> condition(Predicate<boolean[]> condition, Predicate<V> predicate, Observable<V>... observables) {
        if (observables == null || observables.length == 0 || predicate == null) {
            return NEVER;
        }

        return new Observable<Boolean>(observer -> {
            Disposable base = null;
            boolean[] conditions = new boolean[observables.length];

            for (int i = 0; i < observables.length; i++) {
                int index = i;
                Disposable disposable = observables[index].subscribe(value -> {
                    conditions[index] = !predicate.test(value);

                    observer.onNext(condition.test(conditions));
                });
                base = i == 0 ? disposable : base.and(disposable);
            }
            return base;
        });
    }
}
