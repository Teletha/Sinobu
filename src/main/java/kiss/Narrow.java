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

import java.util.function.Supplier;

/**
 * 
 */
public interface Narrow<Assigned, Assigner, Preassigned, Preassigner> extends Flexible {
    /**
     * <p>
     * Apply tail parameter partialy.
     * </p>
     * 
     * @param param A fixed parameter.
     * @return A partial applied function.
     */
    default Assigned assign(Assigner param) {
        return assignLazy(Variable.of(param));
    }

    /**
     * <p>
     * Apply tail parameter partialy.
     * </p>
     * 
     * @param param A fixed parameter.
     * @return A partial applied function.
     */
    default Assigned assignLazy(Supplier<Assigner> param) {
        return I.make(this, Narrow.class, args -> {
            return invoke(I.array(args, param == null ? null : param.get()));
        });
    }

    /**
     * <p>
     * Apply head parameter partialy.
     * </p>
     * 
     * @param param A fixed parameter.
     * @return A partial applied function.
     */
    default Preassigned preassign(Preassigner param) {
        return preassignLazy(Variable.of(param));
    }

    /**
     * <p>
     * Apply head parameter partialy.
     * </p>
     * 
     * @param param A fixed parameter.
     * @return A partial applied function.
     */
    default Preassigned preassignLazy(Supplier<Preassigner> param) {
        return I.make(this, Narrow.class, args -> {
            return invoke(I.array(new Object[] {param == null ? null : param.get()}, args));
        });
    }
}
