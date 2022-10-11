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

public class CGenerator<D extends RegularDD<?, D>> extends CodeGenerator<D> {

    private String paramParamType = "Predicates";

    public CGenerator<D> withParamType(String par) {
        this.paramParamType = par;
        return this;
    }

    @Override
    public void generate(PrintWriter out, List<LabelledRegularDD<D>> roots) {
        out.println("/* This file was generated with the ADD-Lib");
        out.println(" * http://add-lib.scce.info/ */");
        out.println();
        out.println();
        out.println("Predicates predicates;");
        out.println("char* returnstr;");
        out.println();
        for (LabelledRegularDD<D> x0 : roots) {
            returnType(out);
            out.print(" ");
            out.print(x0.label());
            out.print("(");
            paramList(out);
            out.println(");");
        }
        out.println();
        out.println();
        for (LabelledRegularDD<D> x1 : roots) {
            out.println();
            returnType(out);
            out.print(" ");
            out.print(x1.label());
            out.print("(");
            paramList(out);
            out.println(") {");
            out.println("    predicates = pPredicates;");
            out.print("    goto eval");
            out.print(x1.dd().ptr());
            out.println(";");
            out.println("    end:");
            out.println("    return returnstr;");
            out.println();
            for (D x2 : new PreorderTraverser<>(unlabelledRoots(roots))) {
                if (!x2.isConstant()) {
                    out.println();
                    out.print("    eval");
                    out.print(x2.ptr());
                    out.println(":");
                    out.println("    ");
                    out.print("    if (");
                    varCondition(out, x1, x2);
                    out.println("){");
                    out.print("        goto eval");
                    out.print(x2.t().ptr());
                    out.println(";");
                    out.println("    }else{");
                    out.print("        goto eval");
                    out.print(x2.e().ptr());
                    out.println(";");
                    out.println("    }");
                }
            }
            for (D x3 : new PreorderTraverser<>(unlabelledRoots(roots))) {
                if (x3.isConstant()) {
                    out.println();
                    out.print("    eval");
                    out.print(x3.ptr());
                    out.println(":");
                    out.println();
                    out.print("    returnstr = ");
                    resultInstatiation(out, x1, x3);
                    out.println(";");
                    out.println("    goto end;");
                    out.println();
                }
            }
            out.println("}");
        }
        out.println();
    }

    protected void paramList(PrintWriter out) {
        if (this.paramParamType != null) {
            out.print(this.paramParamType);
        }
        out.print(" pPredicates");
    }

    protected void returnType(PrintWriter out) {
        out.print("char*");
    }

    protected void resultInstatiation(PrintWriter out, LabelledRegularDD<D> x1, D x3) {
        out.print("\"");
        out.print(x3.toString());
        out.print("\"");
    }

    protected void varCondition(PrintWriter out, LabelledRegularDD<D> x1, D x2) {
        out.print("predicates.");
        out.print(transformToValidLabel(x2.readName(), x2.readIndex()));
        out.print("()");
    }
}