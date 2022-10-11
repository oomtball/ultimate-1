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

import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;

public class BDDVisitor extends BDDLanguageBaseVisitor<BDD> {

    private final BDDManager ddManager;

    public BDDVisitor(BDDManager ddManager) {
        this.ddManager = ddManager;
    }

    @Override
    public BDD visitNotExpr(BDDLanguageParser.NotExprContext ctx) {

        /* Visit children and apply operation */
        BDD child = visit(ctx.ex);
        BDD result = child.not();

        /* Release memory */
        child.recursiveDeref();
        return result;
    }

    @Override
    public BDD visitAtomExpr(BDDLanguageParser.AtomExprContext ctx) {
        return visit(ctx.ap);
    }

    @Override
    public BDD visitOrExpr(BDDLanguageParser.OrExprContext ctx) {
        /* Visit children and apply operation */
        BDD left = visit(ctx.left);
        BDD right = visit(ctx.right);
        BDD result = left.or(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public BDD visitParenExpr(BDDLanguageParser.ParenExprContext ctx) {
        return visit(ctx.ex);
    }

    @Override
    public BDD visitAndExpr(BDDLanguageParser.AndExprContext ctx) {

        /* Visit children and apply operation */
        BDD left = visit(ctx.left);
        BDD right = visit(ctx.right);
        BDD result = left.and(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public BDD visitTrueExpr(BDDLanguageParser.TrueExprContext ctx) {
        return ddManager.readOne();
    }

    @Override
    public BDD visitFalseExpr(BDDLanguageParser.FalseExprContext ctx) {
        return ddManager.readLogicZero();
    }

    @Override
    public BDD visitVarExpr(BDDLanguageParser.VarExprContext ctx) {
        String varName = ctx.var.getText();
        return ddManager.namedVar(varName);
    }
}
