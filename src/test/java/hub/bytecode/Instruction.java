/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package hub.bytecode;

import org.objectweb.asm.MethodVisitor;

/**
 * @version 2012/01/18 8:37:28
 */
public class Instruction extends Bytecode<Instruction> {

    /** The operation code. */
    public int opcode;

    /**
     * @param opcode
     */
    public Instruction(int opcode) {
        this.opcode = opcode;
    }

    /**
     * @see hub.bytecode.Bytecode#write(org.objectweb.asm.MethodVisitor)
     */
    @Override
    void write(MethodVisitor visitor) {
        visitor.visitInsn(opcode);
    }
}
