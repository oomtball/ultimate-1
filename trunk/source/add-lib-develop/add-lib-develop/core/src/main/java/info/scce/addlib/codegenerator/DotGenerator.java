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
/* This file was generated with the ADD-Lib Gen Tools
 * http://add-lib.scce.info/ */
package info.scce.addlib.codegenerator;

import java.io.PrintWriter;
import java.util.List;

import info.scce.addlib.dd.LabelledRegularDD;
import info.scce.addlib.dd.RegularDD;
import info.scce.addlib.traverser.PreorderTraverser;

public class DotGenerator<D extends RegularDD<?, D>> extends CodeGenerator<D> {

    private String paramGraphName = "ADDLibDecisionService";

    public DotGenerator<D> withGraphName(String gra) {
        this.paramGraphName = gra;
        return this;
    }

    @Override
    public void generate(PrintWriter out, List<LabelledRegularDD<D>> roots) {
        out.println("/* This file was generated with the ADD-Lib");
        out.println(" * http://add-lib.scce.info/ */");
        out.println();
        out.print("digraph \"");
        if (this.paramGraphName != null) {
            out.print(this.paramGraphName);
        }
        out.println("\" {");
        out.println();
        out.println("    bgcolor = transparent");
        out.print("    node [");
        nodeStyle(out);
        out.println("]");
        out.print("    edge [");
        edgeStyle(out);
        out.println("]");
        for (LabelledRegularDD<D> x0 : roots) {
            out.println();
            out.print("    \"f");
            out.print(x0.dd().ptr());
            out.print("\" [");
            ddFunctionStyle(out, x0);
            out.print(", label = \"");
            out.print(x0.label());
            out.println("\"]");
            out.print("    \"f");
            out.print(x0.dd().ptr());
            out.print("\" -> \"n");
            out.print(x0.dd().ptr());
            out.print("\" [");
            ddFunctionThenStyle(out, x0);
            out.println("]");
        }
        for (D x1 : new PreorderTraverser<>(unlabelledRoots(roots))) {
            if (!x1.isConstant()) {
                out.println();
                out.print("    \"n");
                out.print(x1.ptr());
                out.print("\" [");
                internalDdStyle(out, x1);
                out.print(", label = \"");
                internalDdLabel(out, x1);
                out.println("\"]");
                out.print("    \"n");
                out.print(x1.ptr());
                out.print("\" -> \"n");
                out.print(x1.e().ptr());
                out.print("\" [");
                internalDdElseStyle(out, x1);
                out.println("]");
                out.print("    \"n");
                out.print(x1.ptr());
                out.print("\" -> \"n");
                out.print(x1.t().ptr());
                out.print("\" [");
                internalDdThenStyle(out, x1);
                out.println("]");
            }
        }
        for (D x2 : new PreorderTraverser<>(unlabelledRoots(roots))) {
            if (x2.isConstant()) {
                out.println();
                out.print("    \"n");
                out.print(x2.ptr());
                out.print("\" [");
                constantDdStyle(out, x2);
                out.print(", label = \"");
                constantDdLabel(out, x2);
                out.println("\"]");
            }
        }
        out.println("}");
    }

    protected void internalDdThenStyle(PrintWriter out, D x1) {
        out.print("style = solid");
    }

    protected void internalDdElseStyle(PrintWriter out, D x1) {
        out.print("style = dashed");
    }

    protected void ddFunctionStyle(PrintWriter out, LabelledRegularDD<D> x0) {
        out.print("fillcolor = lightgray, shape = rectangle");
    }

    protected void constantDdLabel(PrintWriter out, D x2) {
        out.print(x2.toString());
    }

    protected void ddFunctionThenStyle(PrintWriter out, LabelledRegularDD<D> x0) {
        out.print("style = solid");
    }

    protected void internalDdStyle(PrintWriter out, D x1) {
        out.print("fillcolor = white, shape = ellipse");
    }

    protected void edgeStyle(PrintWriter out) {
        out.print("arrowhead = none");
    }

    protected void nodeStyle(PrintWriter out) {
        out.print("style = filled, fontsize = 14, fontname = Arial");
    }

    protected void internalDdLabel(PrintWriter out, D x1) {
        out.print(x1.readName());
    }

    protected void constantDdStyle(PrintWriter out, D x2) {
        out.print("fillcolor = white, shape = rect");
    }
}
