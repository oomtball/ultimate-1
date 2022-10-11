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

import info.scce.addlib.dd.zdd.ZDD;
import info.scce.addlib.dd.zdd.ZDDManager;

public class ZDDVisitor extends ZDDLanguageBaseVisitor<ZDD> {

    private final ZDDManager ddManager;

    public ZDDVisitor(ZDDManager ddManager) {
        this.ddManager = ddManager;
    }

    @Override
    public ZDD visitStart(ZDDLanguageParser.StartContext ctx) {
        return visit(ctx.ex);
    }

    @Override
    public ZDD visitVarExpr(ZDDLanguageParser.VarExprContext ctx) {
        String varName = ctx.var.getText();
        return ddManager.namedVar(varName);
    }

    @Override
    public ZDD visitZddOneExpr(ZDDLanguageParser.ZddOneExprContext ctx) {
        int i = Integer.parseInt(ctx.i.getText());
        return ddManager.readZddOne(i);
    }

    @Override
    public ZDD visitNotExpr(ZDDLanguageParser.NotExprContext ctx) {

        /* Visit children and apply operation */
        ZDD left = visit(ctx.left);
        ZDD right = visit(ctx.right);
        ZDD result = left.diff(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public ZDD visitAtomExpr(ZDDLanguageParser.AtomExprContext ctx) {
        return visit(ctx.ap);
    }

    @Override
    public ZDD visitChangeExpr(ZDDLanguageParser.ChangeExprContext ctx) {

        /* Visit children and apply operation */
        ZDD child = visit(ctx.ex);
        int var = Integer.parseInt(ctx.v.getText());
        ZDD result = child.change(var);

        /* Release memory */
        child.recursiveDeref();
        return result;
    }

    @Override
    public ZDD visitOrExpr(ZDDLanguageParser.OrExprContext ctx) {

        /* Visit children and apply operation */
        ZDD left = visit(ctx.left);
        ZDD right = visit(ctx.right);
        ZDD result = left.union(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public ZDD visitParenExpr(ZDDLanguageParser.ParenExprContext ctx) {
        return visit(ctx.ex);
    }

    @Override
    public ZDD visitAndExpr(ZDDLanguageParser.AndExprContext ctx) {

        /* Visit children and apply operation */
        ZDD left = visit(ctx.left);
        ZDD right = visit(ctx.right);
        ZDD result = left.intersect(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public ZDD visitTrueExpr(ZDDLanguageParser.TrueExprContext ctx) {
        return ddManager.readOne();
    }

    @Override
    public ZDD visitFalseExpr(ZDDLanguageParser.FalseExprContext ctx) {
        return ddManager.readZero();
    }

}
