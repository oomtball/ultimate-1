/* Copyright (c) 2017-2022, TU Dortmund University
 * This file is part of ADD-Lib, https://add-lib.scce.info/.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of the TU Dortmund University nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package info.scce.addlib.parser;

import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;

public class ADDVisitor extends ADDLanguageBaseVisitor<ADD> {

    private final ADDManager ddManager;

    public ADDVisitor(ADDManager ddManager) {
        this.ddManager = ddManager;
    }

    @Override
    public ADD visitVarExpr(ADDLanguageParser.VarExprContext ctx) {
        String varName = ctx.var.getText();
        return ddManager.namedVar(varName);
    }

    @Override
    public ADD visitRealExpr(ADDLanguageParser.RealExprContext ctx) {
        double value = Double.parseDouble(ctx.real.getText());
        return ddManager.constant(value);
    }

    @Override
    public ADD visitPlusMinusExpr(ADDLanguageParser.PlusMinusExprContext ctx) {

        /* Visit children */
        ADD left = visit(ctx.left);
        ADD right = visit(ctx.right);

        /* Apply operation */
        ADD result;
        char op = ctx.op.getText().charAt(0);
        if (op == '+') {
            result = left.plus(right);
        } else if (op == '-') {
            result = left.minus(right);
        } else {
            throw new IllegalArgumentException("unexpected operator '" + op + "'");
        }

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public ADD visitMulDivExpr(ADDLanguageParser.MulDivExprContext ctx) {

        /* Visit children */
        ADD left = visit(ctx.left);
        ADD right = visit(ctx.right);
        ADD result;

        /* Apply operation */
        char op = ctx.op.getText().charAt(0);
        if (op == '*') {
            result = left.times(right);
        } else if (op == '/') {
            result = left.divide(right);
        } else {
            throw new IllegalArgumentException("unexpected operator '" + op + "'");
        }

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public ADD visitParenExpr(ADDLanguageParser.ParenExprContext ctx) {
        return visit(ctx.ex);
    }
}
