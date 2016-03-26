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

import java.util.Objects;
import java.util.function.Function;

/**
 * @version 2015/04/11 9:03:02
 */
public class Ⅰ<Param> {

    /** The first parameter. */
    public final Param ⅰ;

    /**
     * @param param
     */
    Ⅰ(Param param) {
        this.ⅰ = param;
    }

    /**
     * Create new tuple which replace the first parameter.
     * 
     * @param param New first parameter.
     * @return A created new tuple.
     */
    public <NewParam> Ⅰ<NewParam> a(NewParam param) {
        return I.pair(param);
    }

    /**
     * Create new tuple which calculate the first parameter.
     * 
     * @param calculation A calculation expression.
     * @return A created new tuple.
     */
    public <CalculationResult> Ⅰ<CalculationResult> a(Function<Param, CalculationResult> calculation) {
        return I.pair(calculation.apply(ⅰ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(ⅰ);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Ⅰ) {
            Ⅰ other = (Ⅰ) obj;

            return Objects.equals(ⅰ, other.ⅰ);
        }
        return false;
    }
}
