/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package hub.powerassert;

import hub.ReusableRule;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;

/**
 * @version 2012/01/19 10:47:08
 */
public class PowerAssertTester extends ReusableRule {

    @Rule
    public final PowerAssert POWER_ASSERT = new PowerAssert(this);

    /** For self test. */
    private final List<Operand> expecteds = new ArrayList();

    /** For self test. */
    private final List<String> operators = new ArrayList();

    /**
     * @see hub.ReusableRule#before(java.lang.reflect.Method)
     */
    @Override
    protected void before(Method method) throws Exception {
        expecteds.clear();
        operators.clear();
    }

    /**
     * <p>
     * Validate error message.
     * </p>
     * 
     * @param context
     */
    void validate(PowerAssertContext context) {
        context.toString();
        for (Operand expected : expecteds) {
            if (!context.operands.contains(expected)) {
                throw new AssertionError("Can't capture the below operand.\r\nCode  : " + expected.toString() + "\r\nValue : " + expected.value + "\r\n");
            }
        }

        for (String operator : operators) {
            if (context.stack.peek().toString().indexOf(operator) == -1) {
                throw new AssertionError("Can't capture the below operator.\r\nCode  : " + operator + "\r\n");
            }
        }
    }

    /**
     * @param name
     * @param value
     */
    void willCapture(String name, Object value) {
        expecteds.add(new Operand(name, value));
    }

    /**
     * @param operator
     */
    void willUse(String operator) {
        operators.add(operator);
    }
}
