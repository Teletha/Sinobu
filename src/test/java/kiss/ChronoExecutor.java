/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss;

import static java.util.concurrent.TimeUnit.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ChronoExecutor<E extends ExecutorService> implements ExecutorService {

    /** The flag for task manager. */
    private volatile AtomicBoolean awaiting = new AtomicBoolean();

    /** The non-executed tasks. */
    private volatile CopyOnWriteArraySet remaining = new CopyOnWriteArraySet();

    /** The lazy service initializer. */
    private final Supplier<E> builder;

    /** The service holder. */
    private final AtomicReference<E> holder = new AtomicReference();

    /**
     * By {@link Executors#newCachedThreadPool()}.
     */
    public ChronoExecutor() {
        this(() -> (E) Executors.newCachedThreadPool());
    }

    /**
     * By your {@link ExecutorService}.
     * 
     * @param builder Your {@link ExecutorService}.
     */
    public ChronoExecutor(Supplier<E> builder) {
        this.builder = Objects.requireNonNull(builder);
    }

    /**
     * Retrieve {@link ExecutorService} by lazy initialization.
     * 
     * @return
     */
    protected final E executor() {
        return holder.updateAndGet(e -> e != null ? e : builder.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable command) {
        executor().execute(new Task(command));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        executor().shutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Runnable> shutdownNow() {
        return executor().shutdownNow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShutdown() {
        return executor().isShutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTerminated() {
        return executor().isTerminated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor().awaitTermination(timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Callable<T> command) {
        Task task = new Task(command);

        return task.connect(executor().submit((Callable) task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Runnable command, T result) {
        Task task = new Task(command, result);

        return task.connect(executor().submit((Callable) task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<?> submit(Runnable command) {
        Task task = new Task(command);

        return task.connect(executor().submit((Callable) task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        List<Task<T>> collect = tasks.stream().map(task -> new Task(task)).collect(Collectors.toList());

        return executor().invokeAll(collect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        List<Task<T>> collect = tasks.stream().map(task -> new Task(task)).collect(Collectors.toList());

        return executor().invokeAll(collect, timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        List<Task<T>> collect = tasks.stream().map(task -> new Task(task)).collect(Collectors.toList());

        return executor().invokeAny(collect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        List<Task<T>> collect = tasks.stream().map(task -> new Task(task)).collect(Collectors.toList());

        return executor().invokeAny(collect, timeout, unit);
    }

    /**
     * <p>
     * Wait all task executions.
     * </p>
     */
    public final void await() {
        awaiting.set(true);

        try {
            long start = System.currentTimeMillis();

            while (!remaining.isEmpty()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new Error(e);
                }

                long end = System.currentTimeMillis();

                if (start + 3000 < end && !remaining.isEmpty()) {
                    throw new Error("Task can't exceed 3000ms. Remaining tasks are " + remaining + ".\r\n" + executor());
                }
            }
        } finally {
            awaiting.set(false);
            remaining.clear();
        }
    }

    private long marked;

    /**
     * 
     */
    public ChronoExecutor<E> mark() {
        marked = System.nanoTime();

        return this;
    }

    /**
     * @param start
     * @param end
     * @param unit
     */
    public ChronoExecutor<E> elapseFromMark(int start, TimeUnit unit) {
        long startTime = marked + unit.toNanos(start);

        await(startTime - System.nanoTime(), NANOSECONDS);

        return this;
    }

    /**
     * @param start
     * @param end
     * @param unit
     * @param within
     */
    public ChronoExecutor withinFromMark(int end, TimeUnit unit, Runnable within) {
        if (within != null && System.nanoTime() < marked + unit.toNanos(end)) {
            within.run();
        }
        return this;
    }

    /**
     * <p>
     * Freeze process.
     * </p>
     * 
     * @param millseconds
     */
    public void await(long millseconds) {
        freezeNano(MILLISECONDS.toNanos(millseconds));
    }

    /**
     * <p>
     * Freeze process.
     * </p>
     * 
     * @param millseconds
     */
    public void await(long time, TimeUnit unit) {
        freezeNano(unit.toNanos(time));
    }

    /**
     * <p>
     * Freeze process.
     * </p>
     * 
     * @param time A nano time to freeze.
     */
    private void freezeNano(long time) {
        try {
            long start = System.nanoTime();
            NANOSECONDS.sleep(time);
            long end = System.nanoTime();

            long remaining = start + time - end;

            if (0 < remaining) {
                freezeNano(remaining);
            }
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    /**
     * 
     */
    protected class Task<V> implements Callable<V>, Runnable, Future<V>, ScheduledFuture<V> {

        /** The actual task. */
        private final Callable<V> callable;

        /**
         * @param task
         */
        Task(Runnable task) {
            this(Executors.callable(task));
        }

        /**
         * @param task
         */
        private Task(Runnable task, Object result) {
            this(Executors.callable(task, result));
        }

        /**
         * @param task
         */
        Task(Callable task) {
            this.callable = task;

            // System.out.println("add task " + this);
            remaining.add(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                call();
            } catch (Throwable e) {
                throw new Error(e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V call() throws Exception {
            try {
                return callable.call();
            } finally {
                if (remaining.remove(this)) {
                    // System.out.println("remove task " + this);
                }
            }
        }

        private Future<V> future;

        /**
         * <p>
         * Delegate {@link Future} fuature.
         * </p>
         * 
         * @param future
         * @return
         */
        Task connect(Future<V> future) {
            this.future = future;

            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean cancel = future.cancel(mayInterruptIfRunning);

            if (cancel) {
                if (remaining.remove(this)) {
                    // System.out.println("cancel task " + this);
                }
            }
            return cancel;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDone() {
            return future.isDone();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V get() throws InterruptedException, ExecutionException {
            try {
                return get(1000, MILLISECONDS);
            } catch (TimeoutException e) {
                throw new Error(e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return future.get(timeout, unit);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getDelay(TimeUnit unit) {
            if (future instanceof ScheduledFuture) {
                return ((ScheduledFuture) future).getDelay(unit);
            } else {
                return 0;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Delayed o) {
            if (future instanceof ScheduledFuture) {
                return ((ScheduledFuture) future).compareTo(o);
            } else {
                return 0;
            }
        }
    }
}