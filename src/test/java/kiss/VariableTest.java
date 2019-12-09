/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package kiss;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @version 2018/11/11 10:21:13
 */
public class VariableTest {

    private Variable<String> empty;

    private Variable<String> string;

    @BeforeEach
    void init() {
        empty = Variable.empty();
        string = Variable.of("value");
    }

    @Test
    void of() {
        Variable<String> var = Variable.of("A");
        assert var != null;
        assert var.v != null;
        assert var.v.equals("A");
    }

    @Test
    void ofNull() {
        Variable<String> var = Variable.of((String) null);
        assert var != null;
        assert var.v == null;
    }

    @Test
    void ofOptional() {
        Variable<String> var = Variable.of(Optional.of("A"));
        assert var != null;
        assert var.v != null;
        assert var.v.equals("A");

        var = Variable.of(Optional.empty());
        assert var != null;
        assert var.v == null;
    }

    @Test
    void ofNullOptional() {
        Variable<String> var = Variable.of((Optional) null);
        assert var != null;
        assert var.v == null;
    }

    @Test
    void ofSupplier() {
        Variable<String> var = Variable.of(() -> "A");
        assert var != null;
        assert var.v != null;
        assert var.v.equals("A");

        var = Variable.of(() -> null);
        assert var != null;
        assert var.v == null;
    }

    @Test
    void ofNullSupplier() {
        Variable<String> var = Variable.of((Supplier) null);
        assert var != null;
        assert var.v == null;
    }

    @Test
    void emptyIsNotSingleton() {
        Variable<String> e1 = Variable.empty();
        Variable<String> e2 = Variable.empty();
        assert e1 != e2;
    }

    @Test
    void is() {
        assert empty.is("") == false;
        assert string.is("") == false;
        assert string.is("value");
    }

    @Test
    void isNull() {
        String value = null;
        assert empty.is(value);
        assert string.is(value) == false;
    }

    @Test
    void isCondition() {
        Predicate<String> condition = value -> value == null;
        assert empty.isNot(condition) == false;
        assert string.isNot(condition) == true;
    }

    @Test
    void isNullCondition() {
        Predicate<String> condition = null;
        assert empty.isNot(condition) == true;
        assert string.isNot(condition) == true;
    }

    @Test
    void isNot() {
        assert empty.is("") == false;
        assert string.is("") == false;
        assert string.is("value");
    }

    @Test
    void isNotNull() {
        String value = null;
        assert empty.isNot(value) == false;
        assert string.isNot(value) == true;
    }

    @Test
    void isNotCondition() {
        Predicate<String> condition = value -> value == null;
        assert empty.isNot(condition) == false;
        assert string.isNot(condition) == true;
    }

    @Test
    void isNotNullCondition() {
        Predicate<String> condition = null;
        assert empty.isNot(condition) == true;
        assert string.isNot(condition) == true;
    }

    @Test
    void isToBe() {
        Variable<Boolean> result = string.isToBe("value").to();
        assert result.is(true);

        string.set("change");
        assert result.is(false);
    }

    @Test
    void isToBeNull() {
        Variable<Boolean> result = string.isToBe((String) null).to();
        assert result.is(false);
    }

    @Test
    void isToBeCondition() {
        Variable<Boolean> result = string.isToBe(o -> o.equals("value")).to();
        assert result.is(true);

        string.set("change");
        assert result.is(false);
    }

    @Test
    void isNotToBe() {
        Variable<Boolean> result = string.isNotToBe("value").to();
        assert result.is(false);

        string.set("change");
        assert result.is(true);
    }

    @Test
    void isNotToBeNull() {
        Variable<Boolean> result = string.isNotToBe((String) null).to();
        assert result.is(true);
    }

    @Test
    void isNotToBeCondition() {
        Variable<Boolean> result = string.isNotToBe(o -> o.equals("value")).to();
        assert result.is(false);

        string.set("change");
        assert result.is(true);
    }

    @Test
    void accept() {
        string.accept("ok");
        assert string.is("ok");
    }

    @Test
    void set() {
        assert string.set("change").equals("value");
        assert string.set(() -> "supply").equals("change");
        assert string.set(current -> current + " update").equals("supply");
        assert string.set(Optional.of("optional")).equals("supply update");
        assert string.set(Variable.of("variable")).equals("optional");
        assert string.is("variable");
    }

    @Test
    void setNull() {
        assert string.set((String) null).equals("value");
        assert string.isAbsent();
    }

    @Test
    void setNullSupplier() {
        assert string.set((Supplier<String>) null).equals("value");
        assert string.isAbsent();
    }

    @Test
    void setNullOperator() {
        assert string.set((UnaryOperator<String>) null).equals("value");
        assert string.isAbsent();
    }

    @Test
    void setNullOptional() {
        assert string.set((Optional) null).equals("value");
        assert string.isAbsent();
    }

    @Test
    void setNullVariable() {
        assert string.set((Variable) null).equals("value");
        assert string.isAbsent();
    }

    @Test
    void setIf() {
        assert string.setIf(I.reject(), "change").equals("value");
        assert string.setIf(I.reject(), () -> "supply").equals("value");
        assert string.setIf(I.reject(), current -> current + " update").equals("value");
        assert string.setIf(I.reject(), Optional.of("optional")).equals("value");
        assert string.setIf(I.reject(), Variable.of("variable")).equals("value");
        assert string.is("value");

        assert string.setIf(I.accept(), "change").equals("value");
        assert string.setIf(I.accept(), () -> "supply").equals("change");
        assert string.setIf(I.accept(), current -> current + " update").equals("supply");
        assert string.setIf(I.accept(), Optional.of("optional")).equals("supply update");
        assert string.setIf(I.accept(), Variable.of("variable")).equals("optional");
        assert string.is("variable");
    }

    @Test
    void setIfNullCondition() {
        assert string.setIf(null, "change").equals("value");
        assert string.setIf(null, () -> "supply").equals("change");
        assert string.setIf(null, current -> current + " update").equals("supply");
        assert string.setIf(null, Optional.of("optional")).equals("supply update");
        assert string.setIf(null, Variable.of("variable")).equals("optional");
        assert string.is("variable");
    }

    @Test
    void let() {
        assert string.let("immutable").equals("value");
        assert string.set("failed").equals("immutable");
        assert string.let("failed").equals("immutable");
    }

    @Test
    void letNull() {
        assert string.let((String) null).equals("value");
        assert string.set("failed") == null;
        assert string.let("failed") == null;
    }

    @Test
    void letIf() {
        assert string.letIf(I.reject(), "rejected").equals("value");

        assert string.letIf(I.accept(), "accepted").equals("value");
        assert string.letIf(I.accept(), "failed").equals("accepted");
        assert string.letIf(I.accept(), "failed").equals("accepted");
    }

    @Test
    void letIfNull() {
        assert string.letIf(I.reject(), (String) null).equals("value");
        assert string.setIf(I.reject(), "rejected").equals("value");
        assert string.letIf(I.reject(), "rejected").equals("value");

        assert string.letIf(I.accept(), (String) null).equals("value");
        assert string.setIf(I.accept(), "failed") == null;
        assert string.letIf(I.accept(), "failed") == null;
    }

    @Test
    void letSupplier() {
        Supplier<String> updater = () -> "supplied";
        assert string.let(updater).equals("value");
        assert string.let(updater).equals("supplied");
    }

    @Test
    void letNullSupplier() {
        Supplier<String> updater = null;
        assert string.let(updater).equals("value");
        assert string.let(() -> "failed") == null;
        assert string.let(() -> "failed") == null;
    }

    @Test
    void or() {
        assert empty.or("text").equals("text");
        assert string.or("text").equals("value");
    }

    @Test
    void orNull() {
        String nill = null;
        assert empty.or(nill) == null;
        assert string.or(nill).equals("value");
    }

    @Test
    void mapFunction() {
        Function<String, Integer> size = e -> e.length();
        assert string.map(size).is(5);
        assert empty.map(size).isAbsent();
    }

    @Test
    void mapFunctionNull() {
        assert string.map((Function) null).isAbsent();
        assert empty.map((Function) null).isAbsent();
    }

    @Test
    void flatMap() {
        Function<String, Variable<Integer>> size = e -> Variable.of(e.length());
        assert string.flatMap(size).is(5);
        assert empty.flatMap(size).isAbsent();
    }

    @Test
    void flatMapNull() {
        assert string.flatMap(null).isAbsent();
        assert empty.flatMap(null).isAbsent();
    }

    @Test
    void correctHashAndEqual() {
        Variable<String> one = Variable.of("one");
        Variable<String> other = Variable.of("one");
        assert one != other;
        assert one.hashCode() == other.hashCode();
        assert one.equals(other);
        assert other.equals(one);
    }

    @Test
    void incorrectHashAndEqual() {
        Variable<String> one = Variable.of("one");
        Variable<String> other = Variable.of("other");
        assert one != other;
        assert one.hashCode() != other.hashCode();
        assert one.equals(other) == false;
        assert other.equals(one) == false;
    }

    @Test
    void emptyHashAndEqual() {
        Variable<String> one = Variable.empty();
        Variable<String> other = Variable.empty();
        assert one != other;
        assert one.hashCode() == other.hashCode();
        assert one.equals(other);
        assert other.equals(one);
    }

    @Test
    void observeDispose() throws Exception {
        Variable<String> start = Variable.of("test");
        Variable<String> end = start.observe().take(1).to();
        assert end.isAbsent();
        assert start.signaling.observers.size() == 1;

        start.set("first");
        assert end.is("first");
        assert start.signaling.observers.size() == 0;

        start.set("second");
        assert end.is("first");
    }

    @Test
    void adjust() {
        Variable<String> upper = Variable.<String> empty().adjust(String::toUpperCase);
        upper.set("lower");
        assert upper.get().equals("LOWER");

        // reject null
        assertThrows(NullPointerException.class, () -> upper.set((String) null));
    }

    @Test
    void require() {
        Variable<String> min = Variable.<String> empty().require(v -> v.length() <= 4);
        min.set("ok");
        assert min.get().equals("ok");

        min.set("pass");
        assert min.get().equals("pass");

        min.set("non-qualified");
        assert min.get().equals("pass");

        // reject null
        assertThrows(NullPointerException.class, () -> min.set((String) null));
    }
}
