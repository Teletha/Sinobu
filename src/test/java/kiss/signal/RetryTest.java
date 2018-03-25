/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package kiss.signal;

import java.io.IOError;
import java.nio.channels.IllegalSelectorException;
import java.util.UnknownFormatConversionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

import org.junit.Test;

import kiss.I;

/**
 * @version 2018/03/25 18:53:59
 */
public class RetryTest extends SignalTester {

    @Test
    public void retry() {
        monitor(signal -> signal.startWith("retry").retry());

        assert main.value("retry");
        assert main.emit(Error).value("retry");
        assert main.isNotError();
        assert main.emit(Error).value("retry");
        assert main.isNotError();
        assert main.emit(Error).value("retry");
        assert main.isNotError();
        assert main.emit(Error).value("retry");
        assert main.isNotError();
        assert main.emit(Error).value("retry");
        assert main.isNotError();
    }

    @Test
    public void retryWithLimit() {
        monitor(signal -> signal.startWith("retry").retry(3));

        assert main.value("retry");
        assert main.emit(Error).value("retry");
        assert main.isNotError();
        assert main.emit(Error).value("retry");
        assert main.isNotError();
        assert main.emit(Error).value("retry");
        assert main.isNotError();
        assert main.emit("next will fail", Error).value("next will fail");
        assert main.isError();
        assert main.isDisposed();
    }

    @Test
    public void retryWhenWithDelay() {
        monitor(signal -> signal.startWith("retry").retryWhen(fail -> fail.delay(10, ms)));

        assert main.value("retry");
        assert main.countObservers() == 1;
        assert main.emit(Error).value();
        assert main.hasNoObserver();
        assert await(15).value("retry");
        assert main.countObservers() == 1;
        assert main.emit(Error).value();
        assert main.hasNoObserver();
        assert await(15).value("retry");
        assert main.countObservers() == 1;
    }

    @Test
    public void retryWhenWithDelayAndLimit() {
        monitor(signal -> signal.startWith("retry").retryWhen(fail -> fail.take(2).delay(10, ms)));

        assert main.value("retry");
        assert main.emit(Error).value();
        assert await(30).value("retry");
        assert main.emit(Error).value();
        assert await(30).value("retry");
        assert main.emit(Error).value();
        assert await(30).value();
        assert main.isError();
        assert main.isDisposed();
    }

    @Test
    public void retryWhenWithError() {
        monitor(signal -> signal.startWith("retry")
                .retryWhen(fail -> fail.flatMap(e -> e instanceof Error ? I.signal(e) : I.signalError(e))));

        assert main.value("retry");
        assert main.emit(Error).value("retry");
        assert main.isNotError();
        assert main.emit(Error).value("retry");
        assert main.isNotError();
        assert main.emit("next will fail", IllegalStateException.class).value("next will fail");
        assert main.isError();
        assert main.isDisposed();
    }

    @Test
    public void retryWhenWithComplete() {
        monitor(signal -> signal.retryWhen(fail -> fail.take(2)));

        assert main.emit("first error will retry", Error).value("first error will retry");
        assert main.isNotError();
        assert main.emit("second error will retry", Error).value("second error will retry");
        assert main.isNotError();
        assert main.emit("third error will fail", Error).value("third error will fail");
        assert main.isNotCompleted();
        assert main.isError();
        assert main.isDisposed();
    }

    @Test
    public void disposeRetry() {
        monitor(signal -> signal.startWith("retry").retry(3));

        assert main.value("retry");
        assert main.emit(Error).value("retry");
        assert main.isNotError();

        main.dispose();
        assert main.emit("next will be ignored", Error).value();
        assert main.isNotError();
        assert main.isDisposed();
    }

    @Test
    public void retryThenMerge() {
        monitor(signal -> signal.startWith("retry").retry(3).merge(other.signal()));

        // from main
        assert main.value("retry");
        assert main.emit(Error).value("retry");
        assert main.emit(Error).value("retry");
        assert main.emit(Error).value("retry");

        // from other
        assert other.emit("external").value("external");

        assert main.isNotError();
        assert main.isNotDisposed();

        // dispose
        main.dispose();
        assert main.emit("main is disposed so this value will be ignored").value();
        assert other.emit("other is disposed so this value will be ignored").value();

        assert main.isNotError();
        assert main.isDisposed();
        assert other.isDisposed();
    }

    @Test
    public void retryIf() {
        AtomicBoolean canRetry = new AtomicBoolean(true);
        monitor(signal -> signal.startWith("retry").retryIf(canRetry::get));

        assert main.value("retry");
        assert main.emit(Error).value("retry");
        assert main.emit(Error).value("retry");
        assert main.emit(Error).value("retry");
        assert main.isNotError();

        canRetry.set(false);
        assert main.emit("next will fail", Error).value("next will fail");
        assert main.isError();
        assert main.isDisposed();
    }

    @Test
    public void retryIfNull() {
        monitor(() -> signal(1).effect(log1).retryIf((BooleanSupplier) null));
        assert log1.value(1);
        assert main.value(1);
        assert main.isCompleted();
    }

    @Test
    public void retryUntil() {
        monitor(signal -> signal.retryUntil(other.signal()));

        assert main.emit(Error.class, "Retry any error type").size(1);
        assert main.emit(Exception.class, "Retry any error type").size(1);
        assert main.emit(Throwable.class, "Retry any error type").size(1);
        assert main.isNotError();
        assert main.isNotDisposed();
    }

    @Test
    public void retryUntilByType() {
        monitor(signal -> signal.retryUntil(Error.class, other.signal()));
        assert main.emit(Error.class, "Error can retry").size(1);
        assert main.emit(IOError.class, "Sub type can retry").size(1);
        assert main.isNotError();
        assert main.isNotDisposed();

        assert main.emit(Exception.class, "Exception can't retry").size(0);
        assert main.isError();
        assert main.isDisposed();
    }

    @Test
    public void retryUntilByTypeNull() {
        monitor(signal -> signal.retryUntil(null, other.signal()));
        assert main.emit(Error.class, "null means accepting any error type").size(1);
        assert main.emit(Exception.class, "null means accepting any error type").size(1);
        assert main.emit(IllegalSelectorException.class, "null means accepting any error type").size(1);
        assert main.emit(UnknownFormatConversionException.class, "null means accepting any error type").size(1);
        assert main.isNotError();
        assert main.isNotDisposed();
    }

    @Test
    public void retryUntilByTypeNullNotifier() {
        monitor(signal -> signal.retryUntil(Error.class, null));
        assert main.emit(Error.class, IOError.class, ThreadDeath.class, "null notifier means unconditional").size(1);
        assert main.isNotError();
        assert main.isNotDisposed();

        assert main.emit(Exception.class, "Exception can't retry").size(0);
        assert main.isError();
        assert main.isDisposed();
    }
}
