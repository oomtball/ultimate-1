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

import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.XDDManager;
import org.checkerframework.checker.nullness.qual.Nullable;

public class XDDVisitor<E> extends XDDLanguageBaseVisitor<XDD<E>> {

    private final XDDManager<E> ddManager;

    public XDDVisitor(XDDManager<E> ddManager) {
        this.ddManager = ddManager;
    }

    @Override
    public XDD<E> visitStart(XDDLanguageParser.StartContext ctx) {
        return visit(ctx.ex);
    }

    @Override
    public XDD<E> visitInverseExpr(XDDLanguageParser.InverseExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> child = visit(ctx.ex);
        XDD<E> result = child.inverse();

        /* Release memory */
        child.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitComplExpr(XDDLanguageParser.ComplExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> child = visit(ctx.ex);
        XDD<E> result = child.compl();

        /* Release memory */
        child.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitNotExpr(XDDLanguageParser.NotExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> child = visit(ctx.ex);
        XDD<E> result = child.not();

        /* Release memory */
        child.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitMeetExpr(XDDLanguageParser.MeetExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> left = visit(ctx.left);
        XDD<E> right = visit(ctx.right);
        XDD<E> result = left.meet(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitInfExpr(XDDLanguageParser.InfExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> left = visit(ctx.left);
        XDD<E> right = visit(ctx.right);
        XDD<E> result = left.inf(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitIntersectExpr(XDDLanguageParser.IntersectExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> left = visit(ctx.left);
        XDD<E> right = visit(ctx.right);
        XDD<E> result = left.intersect(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitAndExpr(XDDLanguageParser.AndExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> left = visit(ctx.left);
        XDD<E> right = visit(ctx.right);
        XDD<E> result = left.and(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitMultExpr(XDDLanguageParser.MultExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> left = visit(ctx.left);
        XDD<E> right = visit(ctx.right);
        XDD<E> result = left.mult(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitJoinExpr(XDDLanguageParser.JoinExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> left = visit(ctx.left);
        XDD<E> right = visit(ctx.right);
        XDD<E> result = left.join(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitSupExpr(XDDLanguageParser.SupExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> left = visit(ctx.left);
        XDD<E> right = visit(ctx.right);
        XDD<E> result = left.sup(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitUnionExpr(XDDLanguageParser.UnionExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> left = visit(ctx.left);
        XDD<E> right = visit(ctx.right);
        XDD<E> result = left.union(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitOrExpr(XDDLanguageParser.OrExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> left = visit(ctx.left);
        XDD<E> right = visit(ctx.right);
        XDD<E> result = left.or(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitAddExpr(XDDLanguageParser.AddExprContext ctx) {

        /* Visit children and apply operation */
        XDD<E> left = visit(ctx.left);
        XDD<E> right = visit(ctx.right);
        XDD<E> result = left.add(right);

        /* Release memory */
        left.recursiveDeref();
        right.recursiveDeref();
        return result;
    }

    @Override
    public XDD<E> visitTerminalExpr(XDDLanguageParser.TerminalExprContext ctx) {
        String str = unescapeString(ctx.getText());
        if (str == null) {
            throw new IllegalArgumentException("The terminal expression context returns null!");
        }
        E terminal = ddManager.parseElement(str);
        return ddManager.constant(terminal);
    }

    private @Nullable String unescapeString(String str) {
        if (str.length() > 2) {
            char charFst = str.charAt(0);
            char charLst = str.charAt(str.length() - 1);
            String strBody = str.substring(1, str.length() - 1);
            if (charFst == charLst) {
                if (charFst == '"') {
                    return strBody.replace("\\\"", "\"");
                } else if (charFst == '\'') {
                    return strBody.replace("\\'", "'");
                }
            }
        }
        return null;
    }
}
