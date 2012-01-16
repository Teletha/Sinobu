/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package hub;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;
import hub.Agent.Translator;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kiss.I;
import kiss.Manageable;
import kiss.ThreadSpecific;

import org.junit.Rule;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

/**
 * @version 2012/01/10 9:52:42
 */
public class PowerAssert extends ReusableRule {

    /** The local variable name mapping. */
    private static final Map<Integer, String[]> localVariables = new ConcurrentHashMap();

    @Rule
    private final Agent agent = new Agent(PowerAssertTranslator.class);

    /** The caller class. */
    private final Class caller;

    /** The tester flag. */
    private final boolean selfTest;

    /** The expected operands. */
    private final List<Operand> expecteds = new ArrayList();

    /** The expected operands. */
    private final List<String> operators = new ArrayList();

    /**
     * Assertion Utility.
     */
    public PowerAssert() {
        this.caller = UnsafeUtility.getCaller(1);
        this.selfTest = false;

        // force to transform
        agent.transform(caller);
    }

    /**
     * Test for {@link PowerAssert}.
     */
    PowerAssert(boolean selfTest) {
        this.caller = UnsafeUtility.getCaller(1);
        this.selfTest = selfTest;

        // force to transform
        agent.transform(caller);
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
    void willUseOperator(String operator) {
        operators.add(operator);
    }

    /**
     * @see hub.ReusableRule#before(java.lang.reflect.Method)
     */
    @Override
    protected void before(Method method) throws Exception {
        expecteds.clear();
        operators.clear();
        PowerAssertContext.get().clear();
    }

    /**
     * @see hub.ReusableRule#validateError(java.lang.Throwable)
     */
    @Override
    protected Throwable validateError(Throwable throwable) {
        if (selfTest && throwable instanceof AssertionError) {
            PowerAssertContext context = PowerAssertContext.get();

            for (Operand expected : expecteds) {
                if (!context.operands.contains(expected)) {
                    return new AssertionError("Can't capture the below operand.\r\nCode  : " + expected.name + "\r\nValue : " + expected.value + "\r\n" + context);
                }
            }

            for (String operator : operators) {
                if (context.stack.peek().name.indexOf(operator) == -1) {
                    return new AssertionError("Can't capture the below operator.\r\nCode  : " + operator + "\r\n" + context);
                }
            }
            return null;
        } else {
            return throwable;
        }
    }

    /**
     * @version 2012/01/14 22:48:47
     */
    private static class PowerAssertTranslator extends Translator {

        private static final Type OBJECT_TYPE = Type.getType(Object.class);

        /** The state. */
        private boolean startAssertion = false;

        /** The state. */
        private boolean skipNextJump = false;

        /** The state. */
        private boolean processAssertion = false;

        /**
         * <p>
         * Helper method to write code which load {@link PowerAssertContext}.
         * </p>
         */
        private void loadContext() {
            mv.visitMethodInsn(INVOKESTATIC, "hub/PowerAssert$PowerAssertContext", "get", "()Lhub/PowerAssert$PowerAssertContext;");
        }

        /**
         * <p>
         * Compute simple class name.
         * </p>
         * 
         * @return
         */
        private String computeClassName(String internalName) {
            int index = internalName.lastIndexOf('$');

            if (index == -1) {
                index = internalName.lastIndexOf('/');
            }
            return index == -1 ? internalName : internalName.substring(index + 1);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            if (!startAssertion && opcode == GETSTATIC && name.equals("$assertionsDisabled")) {
                startAssertion = true;
                skipNextJump = true;

                super.visitFieldInsn(opcode, owner, name, desc);
                return;
            }

            super.visitFieldInsn(opcode, owner, name, desc);

            if (processAssertion) {
                loadContext();

                switch (opcode) {
                case GETFIELD:
                    mv.visitLdcInsn(name);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(opcode, owner, name, desc);
                    wrap(Type.getType(desc));
                    invokeVirtual(PowerAssertContext.class, FieldAccess.class);
                    break;

                case GETSTATIC:
                    mv.visitLdcInsn(computeClassName(owner) + '.' + name);
                    mv.visitFieldInsn(opcode, owner, name, desc);
                    wrap(Type.getType(desc));
                    invokeVirtual(PowerAssertContext.class, StaticFieldAccess.class);
                    break;
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitJumpInsn(int opcode, Label label) {
            if (skipNextJump) {
                skipNextJump = false;
                processAssertion = true;

                super.visitJumpInsn(opcode, label);
                return;
            }

            super.visitJumpInsn(opcode, label);

            if (processAssertion) {
                switch (opcode) {
                case IFEQ:
                case IF_ICMPEQ:
                case IF_ACMPEQ:
                    recodeOperator("==");
                    break;

                case IFNE:
                case IF_ICMPNE:
                case IF_ACMPNE:
                    recodeOperator("!=");
                    break;

                case IF_ICMPLT:
                    recodeOperator("<");
                    break;

                case IF_ICMPLE:
                    recodeOperator("<=");
                    break;

                case IF_ICMPGT:
                    recodeOperator(">");
                    break;

                case IF_ICMPGE:
                    recodeOperator(">=");
                    break;

                case IFNULL:
                    // recode null constant
                    loadContext();
                    mv.visitInsn(ACONST_NULL);
                    invokeVirtual(PowerAssertContext.class, Constant.class);

                    // recode == expression
                    recodeOperator("==");
                    break;

                case IFNONNULL:
                    // recode null constant
                    loadContext();
                    mv.visitInsn(ACONST_NULL);
                    invokeVirtual(PowerAssertContext.class, Constant.class);

                    // recode != expression
                    recodeOperator("!=");
                    break;
                }
            }
        }

        /**
         * <p>
         * Helper method to write operator code.
         * </p>
         * 
         * @param operator
         */
        private void recodeOperator(String operator) {
            loadContext();
            mv.visitLdcInsn(operator);
            invokeVirtual(PowerAssertContext.class, Operator.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitTypeInsn(int opcode, String type) {
            if (processAssertion && opcode == NEW && type.equals("java/lang/AssertionError")) {
                processAssertion = false;

                super.visitTypeInsn(opcode, type);
            } else {
                super.visitTypeInsn(opcode, type);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            // replace invocation of AssertionError constructor.
            if (opcode == INVOKESPECIAL && owner.equals("java/lang/AssertionError")) {
                loadContext(); // first parameter
                mv.visitMethodInsn(opcode, owner, name, "(Ljava/lang/Object;)V"); // replace
                return;
            }

            super.visitMethodInsn(opcode, owner, name, desc);

            if (processAssertion) {
                // recode method invocation
                Type methodType = Type.getType(desc);
                Type returnType = methodType.getReturnType();

                mv.visitInsn(DUP);
                mv.visitVarInsn(returnType.getOpcode(ISTORE), 0);

                switch (opcode) {
                case INVOKESTATIC:
                    loadContext();
                    mv.visitLdcInsn(computeClassName(owner) + '.' + name);
                    mv.visitIntInsn(BIPUSH, methodType.getArgumentTypes().length);
                    mv.visitVarInsn(returnType.getOpcode(ILOAD), 0);
                    wrap(returnType);
                    invokeVirtual(PowerAssertContext.class, StaticMethodCall.class);
                    break;

                default:
                    loadContext();
                    mv.visitLdcInsn(name);
                    mv.visitIntInsn(BIPUSH, methodType.getArgumentTypes().length);
                    mv.visitVarInsn(returnType.getOpcode(ILOAD), 0);
                    wrap(returnType);
                    invokeVirtual(PowerAssertContext.class, MethodCall.class);
                    break;
                }
            }
        }

        /**
         * @see org.objectweb.asm.MethodVisitor#visitIincInsn(int, int)
         */
        @Override
        public void visitIincInsn(int index, int increment) {
            super.visitIincInsn(index, increment);

            if (processAssertion) {
                loadContext();
                mv.visitIntInsn(BIPUSH, increment);
                invokeVirtual(PowerAssertContext.class, Increment.class);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitIntInsn(int opcode, int operand) {
            super.visitIntInsn(opcode, operand);

            if (processAssertion) {
                loadContext();
                mv.visitIntInsn(opcode, operand);
                wrap(INT_TYPE);
                invokeVirtual(PowerAssertContext.class, Constant.class);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitInsn(int opcode) {
            super.visitInsn(opcode);

            if (processAssertion) {
                switch (opcode) {
                case ICONST_M1:
                case ICONST_0:
                case ICONST_1:
                case ICONST_2:
                case ICONST_3:
                case ICONST_4:
                case ICONST_5:
                    loadContext();
                    mv.visitInsn(opcode);
                    wrap(INT_TYPE);
                    invokeVirtual(PowerAssertContext.class, Constant.class);
                    break;

                case LCONST_0:
                case LCONST_1:
                    loadContext();
                    mv.visitInsn(opcode);
                    wrap(LONG_TYPE);
                    invokeVirtual(PowerAssertContext.class, Constant.class);
                    break;

                case FCONST_0:
                case FCONST_1:
                case FCONST_2:
                    loadContext();
                    mv.visitInsn(opcode);
                    wrap(FLOAT_TYPE);
                    invokeVirtual(PowerAssertContext.class, Constant.class);
                    break;

                case DCONST_0:
                case DCONST_1:
                    loadContext();
                    mv.visitInsn(opcode);
                    wrap(DOUBLE_TYPE);
                    invokeVirtual(PowerAssertContext.class, Constant.class);
                    break;

                case IADD:
                    recodeOperator("+");
                    break;

                case ISUB:
                    recodeOperator("-");
                    break;

                case IMUL:
                    recodeOperator("*");
                    break;

                case IDIV:
                    recodeOperator("/");
                    break;

                case IREM:
                    recodeOperator("%");
                    break;

                case ISHL:
                    recodeOperator("<<");
                    break;

                case ISHR:
                    recodeOperator(">>");
                    break;

                case IUSHR:
                    recodeOperator(">>>");
                    break;
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitLdcInsn(Object value) {
            super.visitLdcInsn(value);

            if (processAssertion) {
                loadContext();
                mv.visitLdcInsn(value);
                wrap(Type.getType(value.getClass()));
                invokeVirtual(PowerAssertContext.class, Constant.class);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitVarInsn(int opcode, int index) {
            super.visitVarInsn(opcode, index);

            if (processAssertion) {
                Type localVariableType = Type.INT_TYPE;

                switch (opcode) {
                case LLOAD:
                    localVariableType = Type.LONG_TYPE;
                    break;

                case FLOAD:
                    localVariableType = Type.FLOAT_TYPE;
                    break;

                case DLOAD:
                    localVariableType = Type.DOUBLE_TYPE;
                    break;

                case ALOAD:
                    localVariableType = OBJECT_TYPE;
                    break;
                }

                loadContext();
                mv.visitLdcInsn(new Integer(hashCode() + index));
                mv.visitVarInsn(opcode, index);
                wrap(localVariableType);
                invokeVirtual(PowerAssertContext.class, LocalVariable.class);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            super.visitLocalVariable(name, desc, signature, start, end, index);

            localVariables.put(hashCode() + index, new String[] {name, desc});
        }

    }

    /**
     * @version 2012/01/11 11:27:35
     */
    @Manageable(lifestyle = ThreadSpecific.class)
    public static class PowerAssertContext
            implements Constant, FieldAccess, LocalVariable, Operator, MethodCall, StaticFieldAccess, StaticMethodCall,
            Increment {

        /** The operand stack. */
        private ArrayDeque<Operand> stack = new ArrayDeque();

        /** The using operand list. */
        private ArrayList<Operand> operands = new ArrayList();

        /**
         * {@inheritDoc}
         */
        @Override
        public void recodeConstant(Object constant) {
            Operand operand = new Operand(constant);
            stack.add(operand);
            operands.add(operand);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void recodeLocalVariable(int id, Object variable) {
            Operand operand;
            String[] localVariable = localVariables.get(id);

            if (localVariable[1].equals("Z")) {
                operand = new Operand(localVariable[0], (int) variable == 1);
            } else {
                operand = new Operand(localVariable[0], variable);
            }

            stack.add(operand);
            operands.add(operand);
        }

        /**
         * @see hub.PowerAssert.Operator#recodeOperator(java.lang.String)
         */
        @Override
        public void recodeOperator(String expression) {
            if (1 < stack.size()) {
                Operand right = stack.pollLast();
                Operand left = stack.pollLast();
                stack.add(new Operand(left + " " + expression + " " + right, null));
            }
        }

        /**
         * @see hub.PowerAssert.Increment#recodeIncrement(int)
         */
        @Override
        public void recodeIncrement(int increment) {
            switch (increment) {
            case 1:
                stack.add(new Operand(stack.pollLast() + "++", null));
                break;

            case -1:
                stack.add(new Operand(stack.pollLast() + "--", null));
                break;
            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void recodeField(String expression, Object variable) {
            Operand operand = new Operand(stack.pollLast() + "." + expression, variable);
            stack.add(operand);
            operands.add(operand);
        }

        /**
         * @see hub.PowerAssert.StaticFieldAccess#recodeStaticField(java.lang.String,
         *      java.lang.Object)
         */
        @Override
        public void recodeStaticField(String expression, Object variable) {
            Operand operand = new Operand(expression, variable);
            stack.add(operand);
            operands.add(operand);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void recodeMethod(String name, int paramsSize, Object value) {
            // build method invocation
            StringBuilder invocation = new StringBuilder("()");

            for (int i = 0; i < paramsSize; i++) {
                invocation.insert(1, stack.pollLast());

                if (i + 1 != paramsSize) {
                    invocation.insert(1, ", ");
                }
            }
            invocation.insert(0, name).insert(0, '.').insert(0, stack.pollLast());

            Operand operand = new Operand(invocation.toString(), value);
            stack.add(operand);
            operands.add(operand);
        }

        /**
         * @see hub.PowerAssert.StaticMethodCall#recodeStaticMethod(java.lang.String, int,
         *      java.lang.Object)
         */
        @Override
        public void recodeStaticMethod(String name, int paramsSize, Object value) {
            // build method invocation
            StringBuilder invocation = new StringBuilder("()");

            for (int i = 0; i < paramsSize; i++) {
                invocation.insert(1, stack.pollLast());

                if (i + 1 != paramsSize) {
                    invocation.insert(1, ", ");
                }
            }
            invocation.insert(0, name);

            Operand operand = new Operand(invocation.toString(), value);
            stack.add(operand);
            operands.add(operand);
        }

        /**
         * <p>
         * Clear current context.
         * </p>
         */
        public void clear() {
            stack.clear();
            operands.clear();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("\r\n");
            builder.append(stack.peek()).append("\r\n");

            for (Operand operand : operands) {
                if (!operand.constant) {
                    builder.append("\r\n").append(operand.name).append(" : ").append(operand.toValueExpression());
                }
            }
            return builder.toString();
        }

        public static PowerAssertContext get() {
            return I.make(PowerAssertContext.class);
        }
    }

    /**
     * @version 2012/01/11 14:11:46
     */
    private static class Operand {

        /** The human redable expression. */
        private String name;

        /** The actual value. */
        private Object value;

        /** The constant flag. */
        private boolean constant;

        /**
         * 
         */
        private Operand(Object value) {
            if (value instanceof String) {
                this.name = "\"" + value + "\"";
            } else if (value instanceof Class) {
                this.name = ((Class) value).getSimpleName() + ".class";
            } else {
                this.name = String.valueOf(value);
            }
            this.value = value;
            this.constant = true;
        }

        /**
         * 
         */
        private Operand(String name, Object value) {
            this.name = name;
            this.value = value;
            this.constant = false;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Operand other = (Operand) obj;
            if (name == null) {
                if (other.name != null) return false;
            } else if (!name.equals(other.name)) return false;
            if (value == null) {
                if (other.value != null) return false;
            } else if (!value.equals(other.value)) return false;
            return true;
        }

        /**
         * <p>
         * Compute human-readable expression of value.
         * </p>
         * 
         * @return
         */
        private String toValueExpression() {
            if (value == null) {
                return "null";
            } else if (value instanceof String) {
                return "\"" + value + "\"";
            } else if (value instanceof Class) {
                return ((Class) value).getSimpleName() + ".class";
            } else if (value instanceof Enum) {
                Enum enumration = (Enum) value;
                return enumration.getDeclaringClass().getSimpleName() + '.' + enumration.name();
            } else {
                return value.toString();
            }
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * <p>
     * Marker interface for type-safe bytecode builder.
     * </p>
     * 
     * @version 2012/01/14 2:08:48
     */
    private static interface Recodable {
    }

    /**
     * @version 2012/01/14 1:51:05
     */
    private static interface Constant<T> extends Recodable {

        /**
         * <p>
         * Recode constant.
         * </p>
         * 
         * @param constant
         */
        void recodeConstant(T constant);
    }

    /**
     * @version 2012/01/14 1:51:05
     */
    private static interface LocalVariable<T> extends Recodable {

        /**
         * <p>
         * Recode constant.
         * </p>
         * 
         * @param variable
         * @param expression
         */
        void recodeLocalVariable(int id, T variable);
    }

    /**
     * @version 2012/01/14 1:51:05
     */
    private static interface FieldAccess<T> extends Recodable {

        /**
         * <p>
         * Recode constant.
         * </p>
         * 
         * @param expression
         * @param variable
         */
        void recodeField(String expression, T variable);
    }

    /**
     * @version 2012/01/14 1:51:05
     */
    private static interface StaticFieldAccess<T> extends Recodable {

        /**
         * <p>
         * Recode constant.
         * </p>
         * 
         * @param expression
         * @param variable
         */
        void recodeStaticField(String expression, T variable);
    }

    /**
     * @version 2012/01/14 14:42:28
     */
    private static interface Operator<T> extends Recodable {

        /**
         * <p>
         * Recode constant.
         * </p>
         * 
         * @param variable
         * @param expression
         */
        void recodeOperator(String expression);
    }

    /**
     * @version 2012/01/14 14:42:28
     */
    private static interface Increment extends Recodable {

        /**
         * <p>
         * Recode constant.
         * </p>
         */
        void recodeIncrement(int increment);
    }

    /**
     * @version 2012/01/14 14:42:28
     */
    private static interface MethodCall<T> extends Recodable {

        /**
         * <p>
         * Recode constant.
         * </p>
         * 
         * @param variable
         * @param expression
         */
        void recodeMethod(String name, int paramsSize, T value);
    }

    /**
     * @version 2012/01/14 14:42:28
     */
    private static interface StaticMethodCall<T> extends Recodable {

        /**
         * <p>
         * Recode constant.
         * </p>
         * 
         * @param variable
         * @param expression
         */
        void recodeStaticMethod(String name, int paramsSize, T value);
    }
}
