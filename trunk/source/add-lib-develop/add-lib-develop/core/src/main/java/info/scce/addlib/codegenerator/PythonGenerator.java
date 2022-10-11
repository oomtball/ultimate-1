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

public class PythonGenerator<D extends RegularDD<?, D>> extends CodeGenerator<D> {

    private String paramClassName = "ADDLibDecisionService";

    public PythonGenerator<D> withClassName(String cla) {
        this.paramClassName = cla;
        return this;
    }

    @Override
    public void generate(PrintWriter out, List<LabelledRegularDD<D>> roots) {
        out.println("# This file was generated with the ADD-Lib");
        out.println("# http://add-lib.scce.info/");
        out.println();
        out.print("class ");
        if (this.paramClassName != null) {
            out.print(this.paramClassName);
        }
        out.println(":");
        for (LabelledRegularDD<D> x0 : roots) {
            out.println();
            out.print("    def ");
            out.print(x0.label());
            out.print("(self,");
            paramList(out);
            out.println("):");
            out.print("        return self.__eval");
            out.print(x0.dd().ptr());
            out.println("(predicates)");
            out.println("    ");
        }
        for (D x1 : new PreorderTraverser<>(unlabelledRoots(roots))) {
            if (!x1.isConstant()) {
                out.println();
                out.println();
                out.print("    def __eval");
                out.print(x1.ptr());
                out.print("(self,");
                paramList(out);
                out.println("):");
                out.print("        if (");
                varCondition(out, x1);
                out.println("):");
                out.print("            return self.__eval");
                out.print(x1.t().ptr());
                out.println("(predicates)");
                out.println("        else:");
                out.print("            return self.__eval");
                out.print(x1.e().ptr());
                out.println("(predicates)");
            }
        }
        for (D x2 : new PreorderTraverser<>(unlabelledRoots(roots))) {
            if (x2.isConstant()) {
                out.println();
                out.print("    def __eval");
                out.print(x2.ptr());
                out.print("(self,");
                paramList(out);
                out.println("):");
                out.print("        return ");
                resultInstatiation(out, x2);
                out.println("    ");
            }
        }
        out.println("\t");
    }

    protected void paramList(PrintWriter out) {
        out.print("predicates");
    }

    protected void resultInstatiation(PrintWriter out, D x2) {
        out.print("\"");
        out.print(x2.toString());
        out.print("\"");
    }

    protected void varCondition(PrintWriter out, D x1) {
        out.print("predicates.");
        out.print(transformToValidLabel(x1.readName(), x1.readIndex()));
        out.print("()");
    }
}